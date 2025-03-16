package main

import (
	"fmt"
	"log"
	"os"
	"swift-remitly-app/handlers"
	models "swift-remitly-app/models/sql"

	"github.com/gin-gonic/gin"
	"github.com/joho/godotenv"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

func main() {

	err := godotenv.Load()
	if err != nil {
		log.Fatal("Error loading .env file")
	}

	// dsn := "root:qwertyuiop@tcp(127.0.0.1:3306)/remitlyGO"
	dsn := fmt.Sprintf("%s:%s@tcp(%s:%s)/%s",
		os.Getenv("DB_USER"),
		os.Getenv("DB_PASSWORD"),
		os.Getenv("DB_HOST"),
		os.Getenv("DB_PORT"),
		os.Getenv("DB_NAME"),
	)

	fmt.Println("DB_USER:", os.Getenv("DB_USER"))
	fmt.Println("DB_PASSWORD:", os.Getenv("DB_PASSWORD"))
	fmt.Println("DB_HOST:", os.Getenv("DB_HOST"))
	fmt.Println("DB_PORT:", os.Getenv("DB_PORT"))
	fmt.Println("DB_NAME:", os.Getenv("DB_NAME"))
	fmt.Println("DSN:", dsn)

	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})

	if err != nil {
		fmt.Println("Error on database connection: ", err)
		return
	}

	err = db.AutoMigrate(&models.SwiftCode{}, &models.Country{}, &models.Bank{})

	if err != nil {
		fmt.Println("Migration error:", err)
		return
	}

	router := gin.Default()

	router.GET("/v1/swift-codes/:swiftCode", func(c *gin.Context) {
		handlers.GetSwiftCode(c, db)
	})

	router.DELETE("/v1/swift-codes/:swiftCode", func(c *gin.Context) {
		handlers.DeleteSwiftCode(c, db)
	})

	router.GET("/v1/swift-codes/country/:countryISO", func(c *gin.Context) {
		handlers.GetCountry(c, db)
	})

	router.POST("/v1/swift-codes", func(c *gin.Context) {
		handlers.CreateSwiftCode(c, db)
	})

	router.POST("/v1/swift-codes/excel", func(c *gin.Context) {
		handlers.UploadExcelHandler(c)
	})

	router.Run("0.0.0.0:8080")
}
