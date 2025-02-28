package handlers

import (
	"swift-remitly-app/models/dto"
	models "swift-remitly-app/models/sql"

	"github.com/gin-gonic/gin"
	"gorm.io/gorm"
)

func GetSwiftCode(c *gin.Context, db *gorm.DB) {
	swiftCodeParam := c.Param("swiftCode")

	var swiftCode models.SwiftCode

	if err := db.Preload("Country").Preload("Bank").Preload("Branches").Where("CODE = ?", swiftCodeParam).First(&swiftCode).Error; err != nil {
		c.JSON(404, gin.H{"message": "SwiftCode not found"})
		return
	}

	SwiftCodeDTO := dto.MapToSwiftCodeMainDTO(swiftCode)

	c.JSON(200, SwiftCodeDTO)

}
