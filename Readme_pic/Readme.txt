#### 6.4.3 Specific Transaction Workflow
##### 6.4.3.1 Sale Transaction Workflow
![Sale workFlow](D:/Customers/Premier_Bank/Release/Readme_pic/sale.png)
```mermaid
---
title: Sale Transaction Flow (Action-State Architecture)
---
flowchart TD

%% Start
  A["HomePage (MainActivity)"]

%% Workflow
  subgraph SaleTransaction [Sale]
    B["State: ENTER_AMOUNT"]
    B --> C["Action: ActionInputTransData"]

    C --> D["State: CHECK_CARD"]
    D --> E["Action: ActionSearchCard"]

    E --> F["State: EMV_PROC"]
    F --> G["Action: ActionEmvProcess"] 
    
    G --> H["State: TRANS_STATE"]
    H --> I["Action: ActionTransState"]
    
    %% Print Function
    I --> I1["Function: print() -> Print Receipt"]
    I1 --> Z1

  %% Input Amount Failed
    C -->|Input Amount Cancel| Z1["Function: TransEnd() -> Back to Home"]

  %% Card Read Cancel
    E -->|Card Read Cancel| Z1

  %% EMV Process Failed
    G --> |EMV Process Failed| Z1
  end

%% Back to Home
  Z1 --> A
```

##### 6.4.3.2 Refund Transaction Workflow

```mermaid
---
title: Refund Transaction Flow (Action-State Architecture)
---
flowchart TD

%%Start
  A["HomePage (MainActivity)"]

%%Workflow
  subgraph RefundTransaction [Refund]
    B["State: CHECK_PWD"]
    B --> C["Action: ActionInputpwd"]

    C --> D["State: ENTER_DATA"]
    D --> E["Action: ActionInputData"]

    E --> F["State: ENTER_AMOUNT"]
    F --> G["Action: ActionInputTransData"]

    G --> H["State: CHECK_CARD"]
    H --> I["Action: ActionSearchCard"]

    I --> J["State: EMV_PROC"]
    J --> K["Action: ActionEmvProcess"]

    K --> L["State: TRANS_STATE"]
    L --> M["Action: ActionTransState"]

  %%Print Function
    M --> M1["Function: print() -> Print Receipt"]
    M1 --> Z1

  %%Cancel / Error Branches
    C -->|Password Cancel/Error| Z1["Function: TransEnd() -> Back to Home"]
    E -->|RefNo Cancel/Error| Z1
    G -->|Amount Cancel| Z1
    I -->|Card Read Cancel| Z1
    K -->|EMV Process Failed| Z1
  end

%%Back to Home
  Z1 --> A
```
##### 6.4.3.3 Pre-Auth Transaction Workflow
```mermaid
---
title: Pre-Auth Transaction Flow (Action-State Architecture)
---
flowchart TD

%% Interface Entry
  A["HomePage (MainActivity)"]
  A --> B["Page: PreAuthMenu"]
  B --> D["State: ENTER_AMOUNT"]

%% Main Workflow
  subgraph PreAuthTransaction [Pre-Auth]
    D --> E["Action: ActionInputTransData"]

    E --> F["State: CHECK_CARD"]
    F --> G["Action: ActionSearchCard"]

    %% Exception 
    G -->|Card Read Failed| Z1["Function: TransEnd() ¡ú Back to Home"]

    G --> H["State: EMV_PROC"]
    H --> I["Action: ActionEmvProcess"]

    I -->|EMV Failed| Z1

    I --> J["State: TRANS_STATE"]
    J --> K["Action: ActionTransState"]

    K --> K1["Function: print() ¡ú Print Receipt"]
    K1 --> Z1
  end

%% Back to Home
  Z1 --> A
```
##### 6.4.3.4 Pre-Auth-Completion Transaction Workflow
```mermaid
---
title: Pre-Auth-Completion Transaction Flow (Action-State Architecture)
---

flowchart TD

%% Interface Entry
A["HomePage (MainActivity)"]
A --> B["Page: PreAuthMenu"]
B --> D["State: CHECK_PWD"]

%% Main Workflow
subgraph PreAuthCompletion [Pre-Auth-Completion]
D --> E["Action: ActionInputpwd"]
E -->|Cancel| Z1["Function: TransEnd() -> Back to Home"]

E --> F["State: ENTER_DATA"]
F --> G["Action: ActionInputData"]

G --> H["State: TRANS_DETAIL"]
H --> I["Action: ActionDispTransDetail"]

I --> J["State: CHECK_CARD"]
J --> K["Action: ActionSearchCard"]
K -->|Card Read Failed| Z1

K --> L["State: EMV_PROC"]
L --> M["Action: ActionEmvProcess"]
M -->|EMV Failed| Z1

M --> N["State: TRANS_STATE"]
N --> O["Action: ActionTransState"]

O --> O1["Function: print() ¡ú Print Receipt"]
O1 --> Z1
end

%% Back to Home
Z1 --> A
```
##### 6.4.3.5 Pre-Auth-Cancellation Transaction Workflow
```mermaid
---
title: "Pre-Auth-Cancellation Transaction Flow (Action-State Architecture)"
---
flowchart TD

  A["HomePage (MainActivity)"]
  A --> B["Page: PreAuthMenu"]
  B --> D["State: CHECK_PWD"]

  subgraph PreAuthCancellation [Pre-Auth Cancellation]
    D --> E["Action: ActionInputpwd"]
    E -->|Cancel| Z1["Function: TransEnd() -> Back to Home"]

    E --> F["State: ENTER_DATA"]
    F --> G["Action: ActionInputData"]

    G --> H["State: TRANS_DETAIL"]
    H --> I["Action: ActionDispTransDetail"]

    I --> J["State: CHECK_CARD"]
    J --> K["Action: ActionSearchCard"]
    K -->|Card Read Failed| Z1

    K --> L["State: EMV_PROC"]
    L --> M["Action: ActionEmvProcess"]
    M -->|EMV Failed| Z1

    M --> N["State: TRANS_STATE"]
    N --> O["Action: ActionTransState"]

    O --> O1["Function: print() ¡ú Print Receipt"]
    O1 --> Z1
  end

  Z1 --> A
```