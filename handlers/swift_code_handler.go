package handlers

import (
	"fmt"
	"net/http"
	"strings"
	"swift-remitly-app/models/dto"
	models "swift-remitly-app/models/sql"
	"swift-remitly-app/services"

	"github.com/gin-gonic/gin"
	"github.com/go-sql-driver/mysql"
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

func DeleteSwiftCode(c *gin.Context, db *gorm.DB) {
	swiftCodeParam := c.Param("swiftCode")
	var swiftCode models.SwiftCode

	result := db.Where("CODE = ?", swiftCodeParam).First(&swiftCode)
	if result.Error != nil {
		if result.Error == gorm.ErrRecordNotFound {
			c.JSON(http.StatusNotFound, gin.H{"message": "Swift code not found"})
			return
		}
		c.JSON(http.StatusInternalServerError, gin.H{"message": "Database error"})
		return
	}

	if err := swiftCode.RemoveConnections(db); err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"message": "Failed to remove connections"})
		return
	}

	if err := db.Delete(&swiftCode).Error; err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"message": "Failed to delete swift code"})
		return
	}

	c.JSON(200, gin.H{"message": "SwiftCode deleted"})

}

func GetCountry(c *gin.Context, db *gorm.DB) {

	countryISO := c.Param("countryISO")

	var country models.Country

	if err := db.Preload("SwiftCodes").Where("ISO2 = ?", countryISO).First(&country).Error; err != nil {
		if err == gorm.ErrRecordNotFound {
			c.JSON(404, gin.H{"message": "Country not found"})
			return
		}

		c.JSON(500, gin.H{"message": "Internal server error"})
		return
	}

	CountryDTO := dto.MapToCountryDTO(country)

	c.JSON(200, CountryDTO)
}

func CreateSwiftCode(c *gin.Context, db *gorm.DB) {

	var swiftCodeDTO dto.SwiftCodeMainDTO

	if err := c.ShouldBindJSON(&swiftCodeDTO); err != nil {
		c.JSON(400, gin.H{"message": err.Error()})
		return
	}

	if err := dto.ValidateSwiftCodeMainDTO(&swiftCodeDTO); err != nil {
		c.JSON(400, gin.H{"message": err.Error()})
		return
	}

	var country models.Country

	if err := db.Where("ISO2 = ? OR Name = ?", swiftCodeDTO.CountryISO2, swiftCodeDTO.CountryName).First(&country).Error; err != nil {

		if err == gorm.ErrRecordNotFound {

			country = models.Country{
				ISO2: strings.ToUpper(swiftCodeDTO.CountryISO2),
				Name: strings.ToUpper(swiftCodeDTO.CountryName),
			}

			if err := db.Create(&country).Error; err != nil {
				c.JSON(500, gin.H{"message": err.Error()})
				return
			}
		} else {
			c.JSON(500, gin.H{"message": err.Error()})
			return
		}
	}

	var bank models.Bank

	if err := db.Where("Name = ?", swiftCodeDTO.BankName).First(&bank).Error; err != nil {

		if err == gorm.ErrRecordNotFound {

			bank = models.Bank{
				Name: swiftCodeDTO.BankName,
			}

			if err := db.Create(&bank).Error; err != nil {
				c.JSON(500, gin.H{"message": err.Error()})
				return
			}

		} else {
			c.JSON(500, gin.H{"message": err.Error()})
			return
		}

	}

	swiftCodeModel := dto.MapToSwiftCodeModel(swiftCodeDTO)

	if !swiftCodeModel.IsHeadquarter {
		swiftCodeModel.AddConnectionsToBranch(db)
	}

	if err := db.Create(&swiftCodeModel).Error; err != nil {

		if mysqlErr, ok := err.(*mysql.MySQLError); ok && mysqlErr.Number == 1062 {
			c.JSON(400, gin.H{"message": "SwiftCode already exists"})
			return
		} else {
			c.JSON(500, gin.H{"message": err.Error()})
			return
		}

	}

	if swiftCodeModel.IsHeadquarter {
		swiftCodeModel.AddConnectionsToheadquarter(db)
	}

	c.JSON(201, gin.H{"message": "SwiftCode created"})
}

func UploadExcelHandler(c *gin.Context) {

	// Get from body

	file, err := c.FormFile("file")

	if err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"message": "File not found"})
		return
	}

	// Validate mime

	if err := services.ValidateExcelFile(file.Header.Get("Content-Type")); err != nil {
		c.JSON(http.StatusBadRequest, gin.H{"message": err})
		return
	}

	f, err := file.Open()

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"message": "Failed to open file"})
		return
	}

	defer f.Close()

	swiftCodes, err := services.ReadExcelData(f)

	if err != nil {
		c.JSON(http.StatusInternalServerError, gin.H{"message": err})
		return
	}

	//TODO ValidateSwiftCodes

	c.JSON(http.StatusOK, gin.H{"message": fmt.Sprintf("Read %d swift codes", len(swiftCodes))})

}
