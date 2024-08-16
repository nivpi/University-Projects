
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~ FUNCTIONS ~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-- Album, Photo, Order
GO
CREATE	FUNCTION	isValidToJoinDate (@CID Int, @DateToCheck Date)
RETURNS	Bit
AS	BEGIN
	DECLARE	@Valid	Bit
	SELECT @Valid = (CASE	WHEN	DATEDIFF (day, Join_Date, @DateToCheck) >= 0
					THEN 1
					ELSE 0	END)
	FROM CUSTOMERS
	WHERE Customer_ID = @CID
	Return	@Valid
	END
GO

GO
CREATE	FUNCTION	isValidToCreateDate (@CID Int, @Album varchar(20), @URL varchar(80))
RETURNS	Bit
AS	BEGIN
	DECLARE	@Valid	Bit
	SELECT @Valid = (CASE	WHEN	DATEDIFF (day, A.Date_Created, P.Date_Uploaded) >= 0
					THEN 1
					ELSE 0	END)
	FROM ALBUMS AS A CROSS JOIN PHOTOS AS P
	WHERE A.Customer_ID = @CID AND A.Album_Name = @Album AND P.URL = @URL
	Return	@Valid
	END
GO

GO
CREATE	FUNCTION	isValidToDT (@CID Int, @DT Datetime, @URL varchar(80))
RETURNS	Bit
AS	BEGIN
	DECLARE	@Valid	Bit
	SELECT @Valid = (CASE	WHEN	DATEDIFF (day, PR.Project_DT, PH.Date_Uploaded) >= 0
					THEN 1
					ELSE 0	END)
	FROM PROJECTS AS PR CROSS JOIN PHOTOS AS PH
	WHERE PR.Customer_ID = @CID AND PR.Project_DT = @DT AND PH.URL = @URL
	Return	@Valid
	END
GO

GO
CREATE FUNCTION isValidExpiry (@EXP Char(4))
RETURNS Bit
AS BEGIN
	DECLARE @Valid	Bit, @Date Date
	SET @Date = DATEFROMPARTS(2000 + CAST(SUBSTRING(@EXP,3,4) AS Int),CAST(SUBSTRING(@EXP,1,2) AS Int),1)
	SELECT @Valid = (CASE WHEN DATEDIFF (day,getDate(),@Date) > 0
					THEN 1
					ELSE 0	END)
	RETURN @Valid
	END
GO


--SELECT dbo.isValidExpiry('0222')

--drop function dbo.isValidToJoinDate
--drop function dbo.isValidToCreateDate
--drop function dbo.isValidToDT
--drop function dbo.isValidExpiry

--SELECT DATEFROMPARTS(2000 + CAST(SUBSTRING('0226',3,4) AS Int),CAST(SUBSTRING('0226',1,2) AS Int),1)



-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~ CREATES ~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~



CREATE TABLE CUSTOMERS	(
	Customer_ID		Int				NOT NULL,
	Email			varchar(40)		NOT NULL,
	First_Name		varchar(20),
	Last_Name		varchar(20),
	Gender			Char(1),
	Birth_Date		Date,
	Join_Date		Date,
	
	CONSTRAINT PK_CUSTOMERS		PRIMARY KEY (Customer_ID),
	CONSTRAINT AK_Email			UNIQUE (Email),
	CONSTRAINT CK_Gender		CHECK (Gender IN ('M','F')),
	CONSTRAINT CK_Email			CHECK (Email LIKE '%@%.%'),
	CONSTRAINT CK_ValidJoinDate	CHECK (Join_Date < getDate()),		-- #1
	CONSTRAINT CK_DateGap		CHECK (Birth_Date < Join_Date)		-- #2
)


CREATE TABLE INTERESTS	(
	Customer_ID		Int				NOT NULL,
	Interest		varchar(15)		NOT NULL,

	CONSTRAINT PK_INTERESTS		   PRIMARY KEY (Customer_ID, Interest),
	CONSTRAINT FK_CustomerInterest FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS (Customer_ID)
)


CREATE TABLE ALBUMS	(
	Customer_ID		Int				NOT NULL,
	Album_Name		varchar(20)		NOT NULL,
	Date_Created	Date,

	CONSTRAINT PK_ALBUMS		PRIMARY KEY (Customer_ID, Album_Name),
	CONSTRAINT FK_CustomerAlbum FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS (Customer_ID),
	CONSTRAINT CK_DateGapAlbum	CHECK	(dbo.isValidToJoinDate(Customer_ID, Date_Created) = 1)	-- #3
)


CREATE TABLE PHOTOS	(
	URL				varchar(80)		NOT NULL,
	Date_Uploaded	Date,
	Customer_ID		Int				NOT NULL,

	CONSTRAINT PK_PHOTOS		PRIMARY KEY (URL),
	CONSTRAINT FK_CustomerPhoto	FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS (Customer_ID),
	CONSTRAINT CK_DateGapPhoto	CHECK	(dbo.isValidToJoinDate(Customer_ID, Date_Uploaded) = 1)
)


CREATE TABLE ALBUM_PHOTO	(
	Customer_ID		Int				NOT NULL,
	Album_Name		varchar(20)		NOT NULL,
	Photo_URL		varchar(80)		NOT NULL,
	
	CONSTRAINT PK_ALBUM_PHOTO		  PRIMARY KEY (Customer_ID, Album_Name, Photo_URL),
	CONSTRAINT FK_AlbumAlbumsPhotos	  FOREIGN KEY (Customer_ID, Album_Name) REFERENCES ALBUMS (Customer_ID, Album_Name),
	CONSTRAINT FK_PhotoAlbumsPhotos	  FOREIGN KEY (Photo_URL) REFERENCES PHOTOS (URL),
	CONSTRAINT CK_AlbumPhotoDateGap	  CHECK (dbo.isValidToCreateDate(Customer_ID, Album_Name, Photo_URL) = 1)		-- #4
)


CREATE TABLE CATEGORIES	(
	Category_Name	varchar(20)		NOT NULL,
	Parent_Category	varchar(20)		NULL,

	CONSTRAINT PK_CATEGORIES	 PRIMARY KEY (Category_Name),
	CONSTRAINT FK_ParentCategory FOREIGN KEY (Parent_Category) REFERENCES CATEGORIES (Category_Name)
)


CREATE TABLE PRODUCTS	(
	Product_ID		Int				NOT NULL,
	Product_name	varchar(20),
	Size			varchar(20),
	Price			smallmoney,
	in_stock		Bit,
	Category		varchar(20)		NOT NULL,

	CONSTRAINT PK_PRODUCTS			PRIMARY KEY (Product_ID),
	CONSTRAINT FK_CategoryProduct	FOREIGN KEY (Category) REFERENCES CATEGORIES (Category_Name),
	CONSTRAINT CK_Price				CHECK (Price > 0)
)


CREATE TABLE PROJECTS	(
	Customer_ID		Int				NOT NULL,
	DT_Updated		datetime		NOT NULL,
	Project_Name	varchar(30),
	Product_ID		Int				NOT NULL,
	
	CONSTRAINT PK_PROJECTS		  PRIMARY KEY (Customer_ID, DT_Updated),
	CONSTRAINT FK_CustomerProject FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS (Customer_ID),
	CONSTRAINT FK_ProductProject  FOREIGN KEY (Product_ID)  REFERENCES PRODUCTS (Product_ID),
	CONSTRAINT CK_ProjectDTValid  CHECK (dbo.isValidToJoinDate(Customer_ID, DT_Updated) = 1)		-- #5
)

CREATE TABLE PROJECT_PHOTO	(
	Customer		Int				NOT NULL,
	Project_DT		Datetime		NOT NULL,
	Photo_URL		varchar(80)		NOT NULL,
	
	CONSTRAINT PK_PROJECT_PHOTO PRIMARY KEY (Customer, Project_DT, Photo_URL),
	CONSTRAINT FK_ProjectProjectsPhotos	FOREIGN KEY (Customer, Project_DT) REFERENCES PROJECTS (Customer_ID, DT_Updated),
	CONSTRAINT FK_PhotoProjectsPhotos	FOREIGN KEY (Photo_URL) REFERENCES PHOTOS (URL),
)


CREATE TABLE BILLINGS	(
	CC_Number		varchar(20)		NOT NULL,
	CC_Expiry		Char(4),		--format as 'MMYY'
	CC_Holder		varchar(30),
	Street_Name		varchar(20),
	Street_Number	varchar(7),		--building could be '17A'
	City			varchar(20),
	Zip				varchar(10),	--Zip sometimes contain letters

	CONSTRAINT PK_BILLINGS PRIMARY KEY (CC_Number),
	CONSTRAINT CK_ValidExpiry	CHECK (dbo.isValidExpiry(CC_Expiry) = 1)
)


CREATE TABLE CUSTOMER_BILLING	(
	Customer_ID		Int				NOT NULL,
	Credit_Card		varchar(20)		NOT NULL,
	
	CONSTRAINT PK_CUSTOMER_BILLING PRIMARY KEY (Customer_ID, Credit_Card),
	CONSTRAINT FK_CustomerCustomersBillings	FOREIGN KEY (Customer_ID) REFERENCES CUSTOMERS (Customer_ID),
	CONSTRAINT FK_BillingCustomersBillings	FOREIGN KEY (Credit_Card) REFERENCES BILLINGS (CC_Number)
)


CREATE TABLE ORDERS	(
	Order_ID		bigint			NOT NULL,
	Order_DT		smalldatetime,
	Carrier			varchar(15),
	DT_Expected		smalldatetime,
	Credit_Card		varchar(20)		NOT NULL,

	CONSTRAINT PK_ORDERS	PRIMARY KEY (Order_ID),
	CONSTRAINT FK_BillingOrder	FOREIGN KEY (Credit_Card) REFERENCES BILLINGS (CC_Number)
)


CREATE TABLE CONTENTS	(
	[Order]		bigint		NOT NULL,
	Customer	int			NOT NULL,
	Project_DT	Datetime	NOT NULL,
	Quantity	tinyint,
	
	CONSTRAINT PK_CONTENTS PRIMARY KEY ([Order], Customer, Project_DT),
	CONSTRAINT FK_OrderContents   FOREIGN KEY ([Order]) REFERENCES ORDERS (Order_ID),
	CONSTRAINT FK_ProjectContents FOREIGN KEY (Customer, Project_DT) REFERENCES PROJECTS (Customer_ID, DT_Updated),
	CONSTRAINT CK_Quantity		CHECK (Quantity > 0)
)

-- Lookup Table
CREATE TABLE CITIES	(
	City varchar(20)	NOT NULL	PRIMARY KEY)
ALTER TABLE BILLINGS 
Add CONSTRAINT FK_CITY_TO_BILL FOREIGN KEY (City) REFERENCES CITIES(City)



-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~ TASK 1 ~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-- Un-nested Query 1
SELECT C.Customer_ID, [Full Name] = C.First_Name + ' ' + C.Last_Name, [Gift Projects] = COUNT (*)
FROM	CUSTOMERS AS C JOIN PROJECTS AS PRJ ON
	C.Customer_ID = PRJ.Customer_ID JOIN PRODUCTS AS PRD ON
	PRJ.Product_ID = PRD.Product_ID JOIN CATEGORIES AS CAT ON
	PRD.Category = CAT.Category_Name
WHERE CAT.Parent_Category = 'Gifts'
GROUP BY C.Customer_ID, C.First_Name, C.Last_Name
HAVING COUNT (*) >= 5
ORDER BY COUNT (*) DESC, C.Customer_ID DESC


-- Un-nested Query 2
SELECT PRD.Category, Sales = COUNT (*)
FROM PRODUCTS AS PRD JOIN PROJECTS AS PRJ ON PRD.Product_ID = PRJ.Product_ID
	 JOIN CONTENTS AS C ON PRJ.Customer_ID = C.Customer AND PRJ.DT_Updated = C.Project_DT
	 JOIN ORDERS AS O ON C.[Order] = O.Order_ID
WHERE Year(O.Order_DT) = 2022 AND MONTH(O.Order_DT) = 6
GROUP BY PRD.Category
ORDER BY Sales DESC


-- Nested Query 1 - Scalar Output
SELECT [Super Category] = Parent_Category, Projects = COUNT(*), Rate = CONCAT(ROUND(CAST(COUNT(*) AS float) / (
	SELECT COUNT(*)
	FROM PROJECTS
	),2)*100, '%')
FROM PROJECTS AS PRJ JOIN PRODUCTS AS PRD ON PRJ.Product_ID = PRD.Product_ID
	 JOIN CATEGORIES AS CAT ON PRD.Category = CAT.Category_Name
GROUP BY Parent_Category
ORDER BY Rate DESC


-- Nested Query 2 - List Output
SELECT C.Customer, [Kid Gifts Orders] = SUM(Quantity)
FROM CONTENTS AS C JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
WHERE C.Customer IN	(
		SELECT Customer_ID
		FROM INTERESTS
		WHERE Interest = 'Kids'
	) AND P.Product_ID IN (
		SELECT	Product_ID
		FROM	PRODUCTS
		WHERE	Category = 'Gifts for Kids'
	)
GROUP BY C.Customer
ORDER BY [Kid Gifts Orders] DESC


-- Complex Nested Query 1 - Intersect & Except
SELECT Customer_ID
	FROM INTERESTS
	WHERE Interest = 'Travel'
	
	INTERSECT

	SELECT Customer
	FROM CONTENTS
	GROUP BY Customer
	HAVING Sum(Quantity) > (
		SELECT AVG(Items)
		FROM (
			SELECT Customer, Items = SUM (Quantity)
			FROM CONTENTS
			GROUP BY Customer
		) AS A)
	
EXCEPT

	(
	SELECT Customer
	FROM (
		SELECT C.Customer, Items = SUM(C.Quantity)
		FROM CONTENTS AS C JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
			 JOIN PRODUCTS AS PRD ON P.Product_ID = PRD.Product_ID
		WHERE PRD.Category = 'Travel Books'
		GROUP BY C.Customer ) as t1
	WHERE Items <
		(
		SELECT AVG(Items)
		FROM (
			SELECT C.Customer, Items = SUM(C.Quantity)
			FROM CONTENTS AS C JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
					JOIN PRODUCTS AS PRD ON P.Product_ID = PRD.Product_ID
			WHERE PRD.Category = 'Travel Books'
			GROUP BY C.Customer
			) AS B
		)
	)


-- Complex Nested Query 2 - Update
ALTER TABLE PRODUCTS
ADD Year_Income	smallmoney

UPDATE PRODUCTS
SET Year_Income = (
	SELECT ISNULL(SUM (C.Quantity) * PRODUCTS.Price,0)
	FROM CONTENTS AS C JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
	JOIN ORDERS AS O ON C.[Order] = O.Order_ID
	WHERE P.Product_ID = PRODUCTS.Product_ID AND Year(Order_DT) = Year(getDate())
)

SELECT *
FROM PRODUCTS
ORDER BY Year_Income DESC


-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~ TASK 2 ~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-- a VIEW used frequently by the marketing team, to build a mailing list for new promos
GO
CREATE VIEW v_InterestRecommendation AS
	SELECT I.Interest, C.Email
	FROM CUSTOMERS AS C JOIN INTERESTS AS I ON C.Customer_ID = I.Customer_ID
	GROUP BY I.Interest, C.Email
GO
-- an example usage of the object
SELECT Email
FROM v_InterestRecommendation
WHERE Interest = 'Gardening'


-- Function Returning table
GO
CREATE	FUNCTION	BestSellers (@INPUT_City varchar(20))
RETURNS	TABLE
AS
	RETURN
		SELECT	TOP 5	PRD.Product_ID, PRD.Product_name, PRD.Category, PRD.Price, [Total Sold] = SUM(C.Quantity)
		FROM PRODUCTS AS PRD JOIN PROJECTS AS PRJ ON PRD.Product_ID = PRJ.Product_ID
		JOIN CONTENTS AS C ON C.Customer = PRJ.Customer_ID AND C.Project_DT = PRJ.DT_Updated
		JOIN ORDERS AS O ON C.[Order] = O.Order_ID JOIN BILLINGS AS B ON O.Credit_Card = B.CC_Number
		WHERE B.City = @INPUT_City
		GROUP BY PRD.Product_ID, PRD.Product_name, PRD.Category, PRD.Price
		ORDER BY [Total Sold] DESC
GO

SELECT *
FROM dbo.BestSellers('Denver')


-- Trigger
GO
ALTER TABLE CATEGORIES
ADD Total_Products smallint
GO

GO
CREATE TRIGGER	UpdateTotalProductsInCategory
	ON		PRODUCTS
	FOR		INSERT, UPDATE, DELETE
	AS
	UPDATE	CATEGORIES
	SET		Total_Products = (
			SELECT	COUNT(*)
			FROM	PRODUCTS AS P
			WHERE	P.Category = CATEGORIES.Category_Name
			)
	WHERE	Category_Name IN (
			SELECT DISTINCT Category_Name FROM inserted
			UNION
			SELECT DISTINCT Category_Name FROM deleted
			)
GO

SELECT *
FROM CATEGORIES

INSERT INTO PRODUCTS (Product_ID, Category)
VALUES	(51, 'Gifts for Mom'),
		(52, 'Gifts for Mom'),
		(53, 'Travel Books')


-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~ TASK 3 ~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

GO
--DROP View Sales_Trend_Line 
Create View Sales_Trend_Line as
select O.Order_ID, O.Order_DT , C1.Customer, TotalIncome = C1.Quantity * P2.Price
from ORDERS as O join CONTENTS as C1 on O.Order_ID = C1.[Order] join PROJECTS as P1 on C1.Customer = P1.Customer_ID
join PRODUCTS as P2 on P1.Product_ID = P2.Product_ID join CATEGORIES as C3 on P2.Category = C3.Category_Name


--DROP View Sum_Total_Income_Per_Category 
Create View Sum_Total_Income_Per_Category as

select C3.Category_Name , TotalIncome = SUM(C1.Quantity * P2.Price)
from ORDERS as O join CONTENTS as C1 on O.Order_ID = C1.[Order] join PROJECTS as P1 on C1.Customer = P1.Customer_ID
join PRODUCTS as P2 on P1.Product_ID = P2.Product_ID join CATEGORIES as C3 on P2.Category = C3.Category_Name
Group By C3.Category_Name

--DROP VIEW View_Revenue_By_Customer_Age
create view View_Revenue_By_Customer_Age as
select C4.Customer_ID ,[Customer Age] = DATEDIFF(year, C4.Birth_Date, GETDATE()), 
[Revenue By Customer] = sum(DISTINCT(C1.Quantity * P2.Price))
from ORDERS as O join CONTENTS as C1 on O.Order_ID = C1.[Order]
join PROJECTS as P1 on C1.Customer = P1.Customer_ID join CUSTOMERS as C4 
on C4.Customer_ID = P1.Customer_ID join PRODUCTS as P2 on P1.Product_ID = P2.Product_ID
join CATEGORIES as C3 on P2.Category = C3.Category_Name
Group By  C4.Customer_ID, C4.Birth_Date

--DROP VIEW Revenue_By_City
CREATE VIEW Revenue_By_City AS
select B.City as City,
[Revenue by City] = sum(DISTINCT(C1.Quantity * P2.Price)),
[Number of Orders From City] = COUNT(DISTINCT(C4.Customer_ID))
from ORDERS as O join CONTENTS as C1 on O.Order_ID = C1.[Order]
join PROJECTS as P1 on C1.Customer = P1.Customer_ID join CUSTOMERS as C4 
on C4.Customer_ID = P1.Customer_ID join PRODUCTS as P2 on P1.Product_ID = P2.Product_ID
join CUSTOMER_BILLING as C5 on C5.Customer_ID = C4.Customer_ID join BILLINGS as B on
b.CC_Number = C5.Credit_Card join CATEGORIES as C3 on P2.Category = C3.Category_Name
Group By B.city 





--DROP VIEW VIEW_max_min_avg_age
CREATE VIEW VIEW_max_min_avg_age as	
select				[Youngest Customer Age] = (
					SELECT MIN(DATEDIFF(year, C.Birth_Date, GETDATE()))
					),
					[Oldest Customer Age] = (
					SELECT Max(DATEDIFF(year, C.Birth_Date, GETDATE()))
					),
					[AVG Customers Age] = (
					SELECT AVG(DATEDIFF(year, C.Birth_Date, GETDATE()))
					)
					
FROM CUSTOMERS as C

--DROP View View_Revenue_By_TOP10_Customer
create view View_Revenue_By_TOP10_Customer as
select TOP 10 C4.Customer_ID , 
[Revenue By Customer] = sum(DISTINCT(C1.Quantity * P2.Price))
from ORDERS as O join CONTENTS as C1 on O.Order_ID = C1.[Order]
join PROJECTS as P1 on C1.Customer = P1.Customer_ID join CUSTOMERS as C4 
on C4.Customer_ID = P1.Customer_ID join PRODUCTS as P2 on P1.Product_ID = P2.Product_ID
join CATEGORIES as C3 on P2.Category = C3.Category_Name
Group By  C4.Customer_ID
ORDER BY 1 DESC, 2 DESC
GO


-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~ TASK 4 ~~~~~~~~~~~~~~~~~~~~~
-- ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

-- Window Function 1
CREATE VIEW V_CityYearIncome
AS
	SELECT City, [Year] = YEAR(O.Order_DT), Income = SUM (PRD.Price * CNT.Quantity)
	FROM CONTENTS AS CNT JOIN ORDERS AS O ON CNT.[Order] = O.Order_ID
		 JOIN BILLINGS AS B ON O.Credit_Card = B.CC_Number
		 JOIN PROJECTS AS PRJ ON CNT.Customer = PRJ.Customer_ID AND CNT.Project_DT = PRJ.DT_Updated
		 JOIN PRODUCTS AS PRD ON PRJ.Product_ID = PRD.Product_ID
	WHERE YEAR(O.Order_DT) <= 2021
	GROUP BY City, YEAR(O.Order_DT)

SELECT YearlyIncomeRank = (Rank () OVER(PARTITION BY Year ORDER BY Income DESC)), City, [Year], Income,
YearlyGrowth = CONCAT(CAST((Income / (LAG (Income) OVER(PARTITION BY City ORDER BY Year)) -1)*100 AS INT),'%')
FROM V_CityYearIncome
ORDER BY Year DESC, YearlyIncomeRank


-- Window Function 2
GO
CREATE VIEW V_OrderedProjects
AS
SELECT DISTINCT PRJ.Customer_ID, PRJ.DT_Updated, PRJ.Product_ID, Ordered = CASE WHEN (C.[Order] > 0) THEN 1 ELSE 0 END
FROM PROJECTS AS PRJ LEFT JOIN CONTENTS AS C ON PRJ.Customer_ID = C.Customer AND PRJ.DT_Updated = C.Project_DT
GROUP BY PRJ.Customer_ID, PRJ.DT_Updated, PRJ.Product_ID, C.[Order]

GO
CREATE VIEW V_CategorySuccessByYear
AS
SELECT P.Category, [Success Rate] = CAST(100.0 * SUM(Ordered) / COUNT(Ordered) AS DECIMAL(5,2)), [Year] = Year(DT_Updated)
FROM V_OrderedProjects AS V JOIN PRODUCTS AS P ON V.Product_ID = P.Product_ID
GROUP BY P.Category, Year(DT_Updated)
GO


SELECT Category, [Year],
[Year Success Rate (1-6)] = ROW_NUMBER() OVER(PARTITION BY Category ORDER BY [Success Rate] DESC),
[Success Rate] = CONCAT([Success Rate],'%'),
[Success Rate Change] = CONCAT([Success Rate] - LEAD([Success Rate], 1) OVER(PARTITION BY Category ORDER BY [Year]),'%'),
[3 Year Moving Average] = CONCAT(CAST(AVG([Success Rate]) OVER(PARTITION BY Category ORDER BY [Year] ROWS BETWEEN CURRENT ROW AND 2 FOLLOWING) AS DECIMAL(5,2)),'%')
FROM V_CategorySuccessByYear
ORDER BY Category, [Year]



-- Complex Action
--DROP FUNCTION Average_Items_By_Category
GO
CREATE FUNCTION Average_Items_By_Category(@Category varchar(50))
RETURNS REAL
AS
BEGIN
RETURN
(
	SELECT AVG(T.Items * 1.0) FROM
	(
		SELECT C.Customer, Items = SUM(C.Quantity)
		FROM CONTENTS AS C
		JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
		JOIN PRODUCTS AS PRD ON P.Product_ID = PRD.Product_ID
		WHERE PRD.Category = @Category
		GROUP BY C.Customer
	) AS T
)
END
GO

--SELECT dbo.Average_Items_By_Category('Travel Books')

--DROP FUNCTION FrequentlyPurchasingCustomers
GO
CREATE FUNCTION FrequentlyPurchasingCustomers(@Category varchar(50))
RETURNS TABLE
AS
RETURN 
(
	SELECT C.Customer
	FROM CONTENTS AS C
	JOIN PROJECTS AS P ON C.Customer = P.Customer_ID AND C.Project_DT = P.DT_Updated
	JOIN PRODUCTS AS PRD ON P.Product_ID = PRD.Product_ID
	WHERE PRD.Category = @Category
	GROUP BY C.Customer
	HAVING SUM(C.Quantity) > dbo.Average_Items_By_Category(@Category)
)
GO

--SELECT * FROM dbo.FrequentlyPurchasingCustomers('Travel Books')

-- DROP TABLE OLD_INTERESTS
CREATE TABLE OLD_INTERESTS
(
	Customer_ID INT NOT NULL,
	Old_Interest varchar(15) NOT NULL

	PRIMARY KEY (Customer_ID, Old_Interest)
)

--DROP TRIGGER UpdateOldInterests
GO
CREATE TRIGGER UpdateOldInterests
ON INTERESTS
FOR DELETE
AS INSERT INTO OLD_INTERESTS
SELECT D.Customer_ID, D.Interest
FROM DELETED AS D
GO

-- The procedure updates the interest table by getting the frequently
-- purchasing customers of the matching category and deleting
-- the interest from the customer if he is not a frequent buyer any more.
GO
CREATE PROCEDURE UpdateInterestByCategoryPurchases
(
	@Interest varchar(50),
	@Category varchar(50)
)
AS
BEGIN
	DELETE FROM INTERESTS
	WHERE Interest = @Interest AND Customer_ID IN
	(
		SELECT Customer_ID
		FROM INTERESTS
		WHERE Interest = @Interest AND 
		Customer_ID NOT IN 
		(
			SELECT *
			FROM dbo.FrequentlyPurchasingCustomers(@Category)
		)
	)
END
GO

EXEC UpdateInterestByCategoryPurchases
	@Interest = 'Kids', @Category = 'Gifts for Kids'

SELECT *
FROM OLD_INTERESTS