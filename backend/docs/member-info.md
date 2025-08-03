# 會員資料查詢與修改 API 說明

## 流程圖

```mermaid
sequenceDiagram
    participant User as 使用者
    participant FE as 前端
    participant BE as 後端

    Note over User,BE: 查詢會員資料
    User->>FE: 進入個人資料頁面
    FE->>BE: 查詢會員資料 (GET /api/members/profile)
    BE-->>FE: 回傳會員資料
    FE-->>User: 顯示會員資料

    Note over User,BE: 修改會員資料
    User->>FE: 修改個人資料
    FE->>BE: 更新會員資料 (POST /api/members/profile)
    BE-->>FE: 回傳更新後的會員資料
    FE-->>User: 顯示更新成功訊息
```

---

## API 操作步驟

### 1. 查詢會員資料
- **API**：`GET /api/members/profile`
- **Headers**：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```
- **說明**：使用 Access Token 查詢當前登入會員的詳細資料
- **回應**：
```json
{
  "givenName": "小明",
  "familyName": "王",
  "nickName": "明明",
  "birthday": "2000-01-01",
  "subscribe": true,
  "langType": "zh-TW",
  "email": "user@example.com"
}
```
- **說明**：
  - `givenName`: 名字
  - `familyName`: 姓氏
  - `nickName`: 暱稱
  - `birthday`: 生日（YYYY-MM-DD 格式）
  - `subscribe`: 是否訂閱（true/false）
  - `langType`: 語系類型（例如：zh-TW）
  - `email`: 會員電子郵件（不可修改）

### 2. 修改會員資料
- **API**：`POST /api/members/profile`
- **Headers**：
```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
Content-Type: application/json
```
- **Body 範例**：
```json
{
  "givenName": "小明",
  "familyName": "王",
  "nickName": "明明",
  "birthday": "2000-01-01",
  "subscribe": true,
  "langType": "zh-TW"
}
```
- **說明**：
  - 修改會員的基本資料
  - 電子郵件（email）不可修改
  - 所有欄位都是選填，只會更新有提供的欄位
  - 生日格式必須為 YYYY-MM-DD
- **回應**：
```json
{
  "givenName": "小明",
  "familyName": "王",
  "nickName": "明明",
  "birthday": "2000-01-01",
  "subscribe": true,
  "langType": "zh-TW",
  "email": "user@example.com"
}
```
- **說明**：
  - 回傳更新後的完整會員資料
  - 所有欄位都會回傳，包括未修改的欄位

---

## 錯誤處理

### 常見錯誤回應

#### 1. Token 無效或過期
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "401102",
    "message": "Access token invalid",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

#### 2. 會員資料欄位驗證錯誤
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "400106",
    "message": "會員資料欄位錯誤",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": {
      "givenName": "given_name 長度不可超過30",
      "familyName": "family_name 含有不允許的字元",
      "nickName": "nick_name 長度不可超過30",
      "langType": "不支援的語系"
    }
  }
}
```

#### 3. 會員不存在
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "404102",
    "message": "Member not exists",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

#### 4. 伺服器錯誤
```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "500003",
    "message": "發生非預期的系統錯誤，請聯絡系統管理員",
    "timestamp": "2024-01-15T10:30:00Z",
    "details": null
  }
}
```

---

## 錯誤回應格式說明

### 統一錯誤回應結構

所有 API 錯誤都使用統一的 `RestResponse` 格式：

```json
{
  "data": null,
  "meta": null,
  "error": {
    "code": "錯誤代碼",
    "message": "錯誤訊息",
    "timestamp": "錯誤發生時間",
    "details": "詳細錯誤資訊（可為 null 或物件）"
  }
}
```

### 錯誤代碼說明

| 錯誤代碼 | HTTP 狀態碼 | 說明 |
|----------|-------------|------|
| 400106 | 400 | 會員資料欄位驗證錯誤 |
| 401101 | 401 | Access Token 已過期 |
| 401102 | 401 | Access Token 無效 |
| 403101 | 403 | 會員狀態未啟用 |
| 404102 | 404 | 會員不存在 |
| 500003 | 500 | 系統內部錯誤 |

### 欄位驗證錯誤詳解

當發生欄位驗證錯誤時，`error.details` 會包含具體的欄位錯誤資訊：

```json
{
  "details": {
    "givenName": "given_name 長度不可超過30",
    "familyName": "family_name 含有不允許的字元",
    "nickName": "nick_name 長度不可超過30",
    "langType": "不支援的語系"
  }
}
```

### 前端錯誤處理建議

#### 1. 檢查錯誤類型
```javascript
if (response.error) {
  const errorCode = response.error.code;
  const errorMessage = response.error.message;
  const errorDetails = response.error.details;
  
  // 根據錯誤代碼處理不同情況
  switch (errorCode) {
    case '400106':
      // 欄位驗證錯誤，顯示具體錯誤訊息
      handleFieldValidationError(errorDetails);
      break;
    case '401101':
    case '401102':
      // Token 相關錯誤，重新登入
      handleTokenError();
      break;
    case '404102':
      // 會員不存在
      handleMemberNotFound();
      break;
    default:
      // 其他錯誤
      handleGenericError(errorMessage);
  }
}
```

#### 2. 處理欄位驗證錯誤
```javascript
function handleFieldValidationError(errorDetails) {
  if (errorDetails) {
    // 清除之前的錯誤訊息
    clearFieldErrors();
    
    // 顯示各欄位的錯誤訊息
    Object.keys(errorDetails).forEach(fieldName => {
      const errorMessage = errorDetails[fieldName];
      showFieldError(fieldName, errorMessage);
    });
  }
}

function showFieldError(fieldName, message) {
  // 根據欄位名稱找到對應的錯誤顯示元素
  const errorElement = document.querySelector(`[data-field="${fieldName}"] .error-message`);
  if (errorElement) {
    errorElement.textContent = message;
    errorElement.style.display = 'block';
  }
}
```

#### 3. 處理 Token 錯誤
```javascript
function handleTokenError() {
  // 清除本地儲存的 token
  localStorage.removeItem('accessToken');
  localStorage.removeItem('refreshToken');
  
  // 重新導向到登入頁面
  window.location.href = '/login';
}
```

---

## 資料驗證規則

### 會員資料欄位驗證

| 欄位 | 類型 | 必填 | 長度限制 | 格式 | 說明 |
|------|------|------|----------|------|------|
| givenName | String | 否 | 最大 30 字元 | 僅允許字母、數字、空格 | 名字 |
| familyName | String | 否 | 最大 30 字元 | 僅允許字母、數字、空格 | 姓氏 |
| nickName | String | 否 | 最大 30 字元 | 僅允許字母、數字、空格 | 暱稱 |
| birthday | LocalDate | 否 | - | YYYY-MM-DD | 生日 |
| subscribe | Boolean | 否 | - | true/false | 是否訂閱 |
| langType | String | 否 | 最大 10 字元 | 必須是系統支援的語系代碼 | 語系類型 |

### 驗證規則說明

1. **名字和姓氏**：
   - 長度：最大 30 字元
   - 僅允許字母、數字、空格（正則表達式：`^[\\p{L}0-9\\s]+$`）
   - 不能為空字串

2. **暱稱**：
   - 長度：最大 30 字元
   - 僅允許字母、數字、空格（正則表達式：`^[\\p{L}0-9\\s]+$`）
   - 不能為空字串

3. **生日**：
   - 格式：YYYY-MM-DD
   - 必須是有效的日期
   - 不能是未來日期
   - 年齡限制：13-120 歲

4. **訂閱狀態**：
   - 類型：Boolean
   - 可為 true 或 false
   - 用於控制是否接收行銷訊息

5. **語系類型**：
   - 長度：最大 10 字元
   - 格式：必須是系統支援的語言代碼（如 zh-TW, en-US）
   - 預設值：zh-TW
   - 用於設定系統顯示語言
   - 系統會驗證語系代碼是否存在於設定表中

---

## 使用範例

### 前端實作範例

#### 查詢會員資料
```javascript
async function getMemberProfile() {
  try {
    const response = await fetch('/api/members/profile', {
      method: 'GET',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      }
    });
    
    if (response.ok) {
      const profile = await response.json();
      console.log('會員資料:', profile);
      return profile;
    } else {
      throw new Error('查詢失敗');
    }
  } catch (error) {
    console.error('查詢會員資料錯誤:', error);
    throw error;
  }
}
```

#### 修改會員資料
```javascript
async function updateMemberProfile(profileData) {
  try {
    const response = await fetch('/api/members/profile', {
      method: 'POST',
      headers: {
        'Authorization': `Bearer ${accessToken}`,
        'Content-Type': 'application/json'
      },
      body: JSON.stringify(profileData)
    });
    
    const result = await response.json();
    
    if (response.ok) {
      console.log('更新成功:', result);
      return result;
    } else {
      // 處理錯誤回應
      handleApiError(result);
      throw new Error(result.error?.message || '更新失敗');
    }
  } catch (error) {
    console.error('更新會員資料錯誤:', error);
    throw error;
  }
}

// 統一的 API 錯誤處理函數
function handleApiError(response) {
  if (response.error) {
    const errorCode = response.error.code;
    const errorMessage = response.error.message;
    const errorDetails = response.error.details;
    
    switch (errorCode) {
      case '400106':
        // 欄位驗證錯誤
        if (errorDetails) {
          Object.keys(errorDetails).forEach(fieldName => {
            const message = errorDetails[fieldName];
            showFieldError(fieldName, message);
          });
        }
        break;
      case '401101':
      case '401102':
        // Token 錯誤，重新登入
        alert('登入已過期，請重新登入');
        window.location.href = '/login';
        break;
      case '404102':
        alert('會員資料不存在');
        break;
      default:
        alert(`錯誤：${errorMessage}`);
    }
  }
}

// 顯示欄位錯誤訊息
function showFieldError(fieldName, message) {
  const errorElement = document.querySelector(`[data-field="${fieldName}"] .error-message`);
  if (errorElement) {
    errorElement.textContent = message;
    errorElement.style.display = 'block';
  }
}
```

// 使用範例
const profileData = {
  givenName: "小明",
  familyName: "王",
  nickName: "明明",
  birthday: "2000-01-01",
  subscribe: true,
  langType: "zh-TW"
};

updateMemberProfile(profileData)
  .then(profile => {
    // 更新成功，重新渲染頁面
    displayProfile(profile);
  })
  .catch(error => {
    // 顯示錯誤訊息
    showErrorMessage(error.message);
  });
```

---

## HTML 表單範例

### 會員資料編輯表單

```html
<form id="memberProfileForm">
  <div class="form-group" data-field="givenName">
    <label for="givenName">名字</label>
    <input type="text" id="givenName" name="givenName" maxlength="30" 
           pattern="^[\p{L}0-9\s]+$" />
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <div class="form-group" data-field="familyName">
    <label for="familyName">姓氏</label>
    <input type="text" id="familyName" name="familyName" maxlength="30" 
           pattern="^[\p{L}0-9\s]+$" />
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <div class="form-group" data-field="nickName">
    <label for="nickName">暱稱</label>
    <input type="text" id="nickName" name="nickName" maxlength="30" 
           pattern="^[\p{L}0-9\s]+$" />
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <div class="form-group" data-field="birthday">
    <label for="birthday">生日</label>
    <input type="date" id="birthday" name="birthday" />
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <div class="form-group" data-field="subscribe">
    <label>
      <input type="checkbox" id="subscribe" name="subscribe" />
      訂閱行銷訊息
    </label>
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <div class="form-group" data-field="langType">
    <label for="langType">語系</label>
    <select id="langType" name="langType">
      <option value="zh-TW">繁體中文</option>
      <option value="en-US">English</option>
    </select>
    <div class="error-message" style="display: none; color: red;"></div>
  </div>
  
  <button type="submit">更新資料</button>
</form>
```

### 表單提交處理

```javascript
document.getElementById('memberProfileForm').addEventListener('submit', async function(e) {
  e.preventDefault();
  
  // 清除之前的錯誤訊息
  clearFieldErrors();
  
  // 收集表單資料
  const formData = {
    givenName: document.getElementById('givenName').value,
    familyName: document.getElementById('familyName').value,
    nickName: document.getElementById('nickName').value,
    birthday: document.getElementById('birthday').value,
    subscribe: document.getElementById('subscribe').checked,
    langType: document.getElementById('langType').value
  };
  
  try {
    const result = await updateMemberProfile(formData);
    alert('資料更新成功！');
    // 重新載入頁面或更新顯示
    location.reload();
  } catch (error) {
    console.error('更新失敗:', error);
  }
});

// 清除所有欄位錯誤訊息
function clearFieldErrors() {
  document.querySelectorAll('.error-message').forEach(element => {
    element.style.display = 'none';
    element.textContent = '';
  });
}
```

---

## 安全性考量

1. **Token 驗證**：
   - 所有 API 都需要有效的 Access Token
   - Token 過期時需要重新登入或刷新 Token

2. **資料保護**：
   - 電子郵件不可修改，確保帳號安全性
   - 敏感資料不會在回應中暴露

3. **輸入驗證**：
   - 所有輸入都會進行格式驗證
   - 防止 SQL Injection 和 XSS 攻擊

4. **權限控制**：
   - 會員只能修改自己的資料
   - 系統會驗證 Token 中的會員 ID

---

## 常見問題

- **可以修改電子郵件嗎？**
  - 不可以，電子郵件是帳號的唯一識別，不可修改。

- **生日格式有要求嗎？**
  - 必須使用 YYYY-MM-DD 格式，例如：2000-01-01。

- **名字、姓氏、暱稱有什麼限制？**
  - 長度限制：最大 30 字元
  - 字元限制：僅允許字母、數字、空格
  - 不允許特殊符號（如 @、#、$ 等）

- **subscribe 和 langType 欄位是什麼？**
  - `subscribe`：控制是否接收行銷訊息，可設為 true 或 false
  - `langType`：設定系統顯示語言，例如 zh-TW（繁體中文）、en-US（英文）
  - `langType` 長度限制：最大 10 字元，且必須是系統支援的語系代碼

- **暱稱可以重複嗎？**
  - 可以，暱稱不需要唯一性。

- **修改資料後需要重新登入嗎？**
  - 不需要，修改資料不會影響登入狀態。

- **Token 過期怎麼辦？**
  - 使用 Refresh Token 呼叫 `/api/auth/refresh` 取得新的 Access Token。

- **可以只修改部分欄位嗎？**
  - 可以，只需要在請求中提供要修改的欄位即可。

---

## 最佳實踐

1. **錯誤處理**：
   - 實作完整的錯誤處理機制
   - 向用戶顯示友善的錯誤訊息

2. **資料驗證**：
   - 前端和後端都要進行資料驗證
   - 提供即時的輸入驗證回饋

3. **使用者體驗**：
   - 在修改資料時顯示載入狀態
   - 成功修改後顯示確認訊息

4. **安全性**：
   - 定期檢查 Token 有效性
   - 實作適當的登出機制

---

> 若有任何錯誤，API 會回傳友善的錯誤訊息，請依照訊息提示操作。 