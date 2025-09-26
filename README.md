# 線上圖書借閱系統 — Java + Spring Boot 專案開發考題
## 背景
某市的圖書館希望開發一個線上系統, 讓使用者可以查詢各館的館藏並進行借閱與還書, 請你以 Java + Spring Boot 進行實作, 並以真實專案的開發方式撰寫程式碼。

完成後請將專案上傳至 GitHub (公開或提供訪問權限), 並確保程式碼可編譯、可運行。

請盡可能考慮到所有可能的情境。

## 功能需求
### 1. 會員管理
- 角色:
  - 館員(Librarian)
  - 一般用戶(Member)
- 功能:
  - 使用者可註冊、登入。
  - 建立館員帳號時,需與圖書館另一系統驗證(模擬 API 呼叫):
    - GET
      - Header: { "Authorization": "todo" }
      - URL: https://todo.com.tw
  - 權限控制:
    - 館員可管理書籍資料。
    - 一般用戶不可新增/修改書籍資料。

### 2. 書籍管理
- 書籍資訊:
  - 書名
  - 作者
  - 出版年份
  -  類型(圖書、書籍)
- 功能:
  - 館員可新增書籍至系統(支援多館館藏與同館多副本)。
  - 使用者可依書名、作者、年份搜尋書籍。
  - 搜尋結果需顯示:
    - 書籍基本資訊
    - 各館館藏數量 (可用數量)。

### 3. 借閱與還書
- 借閱規則:
  - 每位使用者同時最多可借:
    - 圖書:5 本
    - 書籍:10 本
  - 借閱期限:1 個月。
- 功能:
  - 借書與還書功能。
  - 到期前 5 天需發送通知 
  (實作可用 System.out.println 模擬訊息發送)。
  - 借閱數量或期限超過規則時需提示錯誤。

## 如何開始

### 專案技術棧
- 後端框架: Spring Boot 3.x, Spring Security (JWT)
- 資料庫: PostgreSQL (透過 JPA/Hibernate)
- API 文件: Springdoc-OpenAPI (Swagger UI)
- 構建工具: Maven

### 環境要求
在開始之前，請確保你的系統已安裝以下軟體：
- JDK 17 或更高版本
- Maven 3.6 或更高版本
- Docker Desktop
  
### 啟動資料庫
執行以下命令來啟動 PostgreSQL 容器：
```bash
docker-compose up -d
```

### 執行與除錯
執行 Maven 命令來建置與運行專案：
```bash
# 打包專案成 .jar
mvn clean package -DskipTests

# 運行專案
mvn spring-boot:run -Dspring-boot.run.profiles=local
```

當你打包好 .jar 也可以使用 Docker 運行專案
```bash
# 建立 Docker Image
docker build -t linelms:0.0.1 .

# 執行 Docker Image
docker run -it --rm --name linelms -p 8080:8080 linelms:0.0.1
```

### API 文件
當 Server 成功啟動後，開啟瀏覽器輸入以下網址：
```bash
http://localhost:8080/swagger-ui.html
```
可以存取 Swagger UI 測試和理解 API。

### 測試流程（JWT 認證）
1. 註冊帳號: 呼叫 POST /api/auth/register，建立館員(LIBRARIAN)或一般用戶(Member)
2. 登入: 呼叫 POST /api/auth/login，獲取 JWT Token。
3. 設定 Token: 設定 Header Authorize（格式為：Bearer eyJ...）。
4. 操作: 測試需要 LIBRARIAN 權限的 API（例如：POST /api/books/add），或使用 Member 測試借閱功能。