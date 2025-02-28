package main

import (
	"fmt"
	"swift-remitly-app/handlers"
	models "swift-remitly-app/models/sql"

	"github.com/gin-gonic/gin"
	"gorm.io/driver/mysql"
	"gorm.io/gorm"
)

func main() {

	dsn := "root:qwertyuiop@tcp(127.0.0.1:3306)/remitlyGO"

	db, err := gorm.Open(mysql.Open(dsn), &gorm.Config{})

	if err != nil {
		fmt.Println("Błąd połączenia z bazą danych:", err)
		return
	}

	err = db.AutoMigrate(&models.SwiftCode{}, &models.Country{}, &models.Bank{})

	if err != nil {
		fmt.Println("Bład migracji:", err)
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

	router.Run("localhost:8080")
}
