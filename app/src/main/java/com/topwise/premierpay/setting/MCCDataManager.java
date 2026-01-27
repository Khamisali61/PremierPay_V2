package com.topwise.premierpay.setting;

import android.content.Context;
import android.util.Log;

import com.topwise.manager.AppLog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class MCCDataManager {
    private static final String TAG = "MCCDataManager";

    public static List<MCCItem> loadMCCData(Context context) {
        List<MCCItem> mccList = new ArrayList<>();
        Long startTime = System.currentTimeMillis();
        AppLog.d(TAG, "loadMCCData start: " + startTime);

        try {
            InputStream is = context.getAssets().open("mcc_codes.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String jsonString = new String(buffer, StandardCharsets.UTF_8);

            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray mccArray = jsonObject.getJSONArray("mcc_codes");

            for (int i = 0; i < mccArray.length(); i++) {
                JSONObject item = mccArray.getJSONObject(i);
                MCCItem mccItem = parseMCCItem(item);
                if (mccItem != null) {
                    mccList.add(mccItem);
                }
            }
            AppLog.d(TAG, "loadMCCData time used(ms): " + (System.currentTimeMillis() - startTime));

        } catch (Exception e) {
            AppLog.e(TAG, "Error loading MCC data" + e);
            mccList = createDefaultData();
        }

        return mccList;
    }

    // 获取范围内的所有具体MCC代码
    public static List<String> getRangeMCCCodes(MCCItem rangeItem) {
        List<String> codes = new ArrayList<>();
        if (rangeItem.isRange() && rangeItem.getMinCode() != null && rangeItem.getMaxCode() != null) {
            for (int i = rangeItem.getMinCode(); i <= rangeItem.getMaxCode(); i++) {
                codes.add(String.format("%04d", i));
            }
        }
        return codes;
    }

    private static MCCItem parseMCCItem(JSONObject item) {
        try {
            MCCItem mccItem = new MCCItem();
            mccItem.setCode(item.getString("code"));
            mccItem.setDescription(item.getString("description"));
            mccItem.setCategory(item.getString("category"));
            mccItem.setRange(item.getBoolean("isRange"));

            if (mccItem.isRange()) {
                mccItem.setMinCode(item.getInt("minCode"));
                mccItem.setMaxCode(item.getInt("maxCode"));
            }

            return mccItem;
        } catch (Exception e) {
            Log.e(TAG, "Error parsing MCC item", e);
            return null;
        }
    }


    // 根据代码查找描述
    public static String getDescriptionByCode(List<MCCItem> mccList, String code) {
        // 首先查找精确匹配
        for (MCCItem item : mccList) {
            if (!item.isRange() && item.getCode().equals(code)) {
                return item.getDescription();
            }
        }

        // 如果在范围内，返回范围描述
        try {
            int codeValue = Integer.parseInt(code);
            for (MCCItem item : mccList) {
                if (item.isRange() &&
                        codeValue >= item.getMinCode() &&
                        codeValue <= item.getMaxCode()) {
                    return item.getDescription() + " (" + code + ")";
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid MCC code: " + code);
        }

        return "Unknown MCC";
    }
    // 根据code查找item在list中的位置（精确匹配）
    public static int findPositionByCode(List<MCCItem> mccList, String code) {
        if (mccList == null || code == null) {
            return -1;
        }

        for (int i = 0; i < mccList.size(); i++) {
            MCCItem item = mccList.get(i);
            if (code.equals(item.getCode())) {
                return i;
            }
        }
        return -1;
    }


    // 根据code查找item在list中的位置（支持范围匹配）
    public static int findPositionByCodeIncludingRanges(List<MCCItem> mccList, String code) {
        if (mccList == null || code == null) {
            return -1;
        }

        // 首先尝试精确匹配
        int exactMatch = findPositionByCode(mccList, code);
        if (exactMatch != -1) {
            return exactMatch;
        }

        // 如果精确匹配失败，尝试范围匹配
        try {
            int codeValue = Integer.parseInt(code);
            for (int i = 0; i < mccList.size(); i++) {
                MCCItem item = mccList.get(i);
                if (item.isRange() &&
                        codeValue >= item.getMinCode() &&
                        codeValue <= item.getMaxCode()) {
                    return i; // 返回范围项的位置
                }
            }
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid MCC code for range matching: " + code);
        }

        return -1;
    }

    // 根据code获取MCCItem对象（精确匹配）
    public static MCCItem findItemByCode(List<MCCItem> mccList, String code) {
        int position = findPositionByCode(mccList, code);
        if (position != -1 && position < mccList.size()) {
            return mccList.get(position);
        }
        return null;
    }

    // 根据code获取MCCItem对象（支持范围匹配）
    public static MCCItem findItemByCodeIncludingRanges(List<MCCItem> mccList, String code) {
        int position = findPositionByCodeIncludingRanges(mccList, code);
        if (position != -1 && position < mccList.size()) {
            return mccList.get(position);
        }
        return null;
    }

    // 获取范围内所有可能的代码及其位置映射
    public static java.util.Map<String, Integer> createCodeToPositionMap(List<MCCItem> mccList) {
        java.util.Map<String, Integer> map = new java.util.HashMap<>();

        for (int i = 0; i < mccList.size(); i++) {
            MCCItem item = mccList.get(i);
            if (!item.isRange()) {
                // 单个代码直接映射
                map.put(item.getCode(), i);
            } else {
                // 范围代码：生成范围内的所有代码映射到范围项的位置
                for (int codeValue = item.getMinCode(); codeValue <= item.getMaxCode(); codeValue++) {
                    String codeStr = String.format("%04d", codeValue);
                    map.put(codeStr, i);
                }
            }
        }

        return map;
    }

    // 批量查找多个代码的位置
    public static List<Integer> findPositionsByCodes(List<MCCItem> mccList, List<String> codes) {
        List<Integer> positions = new ArrayList<>();
        for (String code : codes) {
            int position = findPositionByCodeIncludingRanges(mccList, code);
            positions.add(position);
        }
        return positions;
    }
    private static List<MCCItem> createDefaultData() {
        List<MCCItem> defaultList = new ArrayList<>();

        // Merchandise / Retail
        defaultList.add(new MCCItem("0081", "Agricultural Co-operative", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("0742", "Veterinary Services", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("0763", "Agricultural Co-operatives", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("0780", "Horticultural Services", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1520", "General Contracting", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1711", "Heating, Plumbing & A/C", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1731", "Electrical Contractors", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1740", "Masonry, Stonework, Plaster", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1750", "Carpentry Contractors", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1761", "Roofing/Siding/Sheet Metal", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1771", "Concrete Work Contractors", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("1799", "Special Trade Contractors", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("2741", "Misc. Publishing & Printing", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("2791", "Typesetting/Engraving", false, null, null, "Merchandise/Retail"));
        defaultList.add(new MCCItem("2842", "Specialty Cleaning", false, null, null, "Merchandise/Retail"));

        // Travel / Hospitality (范围类型)
        defaultList.add(new MCCItem("3000-3299", "Airlines", true, 3000, 3299, "Travel/Hospitality"));
        defaultList.add(new MCCItem("3300-3499", "Car Rental Agencies", true, 3300, 3499, "Travel/Hospitality"));
        defaultList.add(new MCCItem("3500-3999", "Lodging", true, 3500, 3999, "Travel/Hospitality"));

        // Automotive
        defaultList.add(new MCCItem("3351-3441", "Automotive Rentals", true, 3351, 3441, "Automotive"));
        defaultList.add(new MCCItem("3501", "Vehicle Maintenance Services", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5013", "Motor Vehicle Parts", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5039", "Construction Materials", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5531", "Auto Stores – Retail", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5532", "Automotive Tire Stores", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5533", "Automotive Parts & Accessories", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5541", "Fuel Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5542", "Automated Fuel Dispensers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5543", "Automated Truck/Diesel Fuel", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5551", "Boat Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5561", "Camper/Recreational Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5571", "Motorcycle Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5592", "Motor Home Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5598", "Snowmobile Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5599", "Misc. Automotive Dealers", false, null, null, "Automotive"));
        defaultList.add(new MCCItem("5983", "Fuel Oil & Coal Dealers", false, null, null, "Automotive"));

        // Clothing / Apparel
        defaultList.add(new MCCItem("5137", "Men’s/Women’s Clothing", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5139", "Commercial Footwear", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5651", "Family Clothing Stores", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5655", "Sports Apparel/Riding Apparel", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5661", "Shoe Stores", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5691", "Men’s/Women’s Clothing", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5698", "Wig & Toupee Stores", false, null, null, "Clothing/Apparel"));
        defaultList.add(new MCCItem("5699", "Misc. Apparel Shops", false, null, null, "Clothing/Apparel"));

        // Food & Grocery
        defaultList.add(new MCCItem("5422", "Meat Provisioners", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5441", "Candy, Nut & Confectionary Stores", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5451", "Dairy Stores", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5411", "Grocery/Supermarkets", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5462", "Bakeries", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5499", "General Food Stores", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5811", "Caterers", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5812", "Restaurants", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5813", "Bars/Clubs", false, null, null, "Food & Grocery"));
        defaultList.add(new MCCItem("5814", "Fast Food", false, null, null, "Food & Grocery"));

        // Healthcare
        defaultList.add(new MCCItem("5047", "Dental Equipment", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("5122", "Drugs/Proprietary Stores", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("5295", "Medical Equipment", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("5912", "Drug Stores/Pharmacies", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8011", "Doctors", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8021", "Dentists", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8031", "Osteopaths", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8041", "Chiropractors", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8042", "Optometrists/Ophthalmologists", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8043", "Opticians/Prescription", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8044", "Opticians/Non-Prescription", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8049", "Misc. Medical Practitioners", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8050", "Nursing & Personal Care", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8062", "Hospitals", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8071", "Medical/Dental Labs", false, null, null, "Healthcare"));
        defaultList.add(new MCCItem("8099", "Misc. Health Practitioners", false, null, null, "Healthcare"));

        // Home / Furniture
        defaultList.add(new MCCItem("5021", "Furniture Stores", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5039", "Construction Materials", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5712", "Furniture/Home Furnishings", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5713", "Floor Covering Stores", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5714", "Drapery/Window Cover", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5718", "Fireplace/Shops", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5719", "Misc. Home Furnishing", false, null, null, "Home/Furniture"));
        defaultList.add(new MCCItem("5722", "Household Appliances", false, null, null, "Home/Furniture"));

        // Electronics
        defaultList.add(new MCCItem("5045", "Computers/Computer Peripherals", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("5732", "Electronics Stores", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("5733", "Music Stores", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("5734", "Computer Software Stores", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("5735", "Record Shops", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("4812", "Telecom Equipment", false, null, null, "Electronics"));
        defaultList.add(new MCCItem("4814", "Telecom Services", false, null, null, "Electronics"));

        // General Services
        defaultList.add(new MCCItem("4111", "Local Transport (Taxi, Ride-Hail)", false, null, null, "General Services"));
        defaultList.add(new MCCItem("4131", "Bus Lines", false, null, null, "General Services"));
        defaultList.add(new MCCItem("4814", "Telecommunication Services", false, null, null, "General Services"));
        defaultList.add(new MCCItem("4900", "Utilities", false, null, null, "General Services"));
        defaultList.add(new MCCItem("5039", "Construction Materials", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7333", "Commercial Photography", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7372", "Computer Programming", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7379", "Computer Maintenance", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7392", "Management Consulting", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7393", "Detective Agencies", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7394", "Equipment Rental", false, null, null, "General Services"));
        defaultList.add(new MCCItem("7399", "Business Services", false, null, null, "General Services"));

        // Travel / Hospitality (具体类型)
        defaultList.add(new MCCItem("4112", "Passenger Railways", false, null, null, "Travel/Hospitality"));
        defaultList.add(new MCCItem("4722", "Travel Agencies", false, null, null, "Travel/Hospitality"));
        defaultList.add(new MCCItem("7011", "Lodging/Hotels/Motels", false, null, null, "Travel/Hospitality"));
        defaultList.add(new MCCItem("7032", "Sporting Camps", false, null, null, "Travel/Hospitality"));
        defaultList.add(new MCCItem("7033", "Trailer Parks", false, null, null, "Travel/Hospitality"));

        // Entertainment
        defaultList.add(new MCCItem("7832", "Motion Picture Theatres", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7922", "Theatrical Producers", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7932", "Billiard/Pool", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7933", "Bowling Centers", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7991", "Tourist Attractions", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7992", "Golf Courses", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7994", "Video Game Arcades", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7996", "Amusement Parks", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7997", "Membership Clubs", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7998", "Aquariums & Zoos", false, null, null, "Entertainment"));
        defaultList.add(new MCCItem("7999", "Misc. Recreation Services", false, null, null, "Entertainment"));

        // Education
        defaultList.add(new MCCItem("8211", "Schools K-12", false, null, null, "Education"));
        defaultList.add(new MCCItem("8220", "Colleges/Universities", false, null, null, "Education"));
        defaultList.add(new MCCItem("8241", "Correspondence Schools", false, null, null, "Education"));
        defaultList.add(new MCCItem("8244", "Business/Secretarial Schools", false, null, null, "Education"));
        defaultList.add(new MCCItem("8249", "Vocational/Trade", false, null, null, "Education"));
        defaultList.add(new MCCItem("8299", "Other Education", false, null, null, "Education"));

        // Financial / Insurance
        defaultList.add(new MCCItem("6010", "ATM Cash Withdrawal", false, null, null, "Financial/Insurance"));
        defaultList.add(new MCCItem("6011", "Financial Institutions", false, null, null, "Financial/Insurance"));
        defaultList.add(new MCCItem("6051", "Non-Financial Institutions", false, null, null, "Financial/Insurance"));
        defaultList.add(new MCCItem("6211", "Security Brokers", false, null, null, "Financial/Insurance"));
        defaultList.add(new MCCItem("6300", "Insurance Premiums", false, null, null, "Financial/Insurance"));
        defaultList.add(new MCCItem("6399", "Misc. Insurance", false, null, null, "Financial/Insurance"));

        // Real Estate
        defaultList.add(new MCCItem("6513", "Real Estate Agents/Managers", false, null, null, "Real Estate"));

        // Utilities
        defaultList.add(new MCCItem("4814", "Telecom Services", false, null, null, "Utilities"));
        defaultList.add(new MCCItem("4900", "Electric/Gas/Water", false, null, null, "Utilities"));
        defaultList.add(new MCCItem("4899", "Cable/Satellite Providers", false, null, null, "Utilities"));

        // Government / Non-Profit
        defaultList.add(new MCCItem("8211", "Schools", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("8398", "Charitable Organizations", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("8651", "Political Organizations", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("8661", "Religious Organizations", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("9211", "Court Costs", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("9222", "Fines", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("9311", "Tax Payments", false, null, null, "Government/Non-Profit"));
        defaultList.add(new MCCItem("9399", "Misc. Government Services", false, null, null, "Government/Non-Profit"));

        return defaultList;
    }

}
