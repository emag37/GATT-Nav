EESchema Schematic File Version 4
LIBS:gatt_nav-cache
EELAYER 29 0
EELAYER END
$Descr A4 11693 8268
encoding utf-8
Sheet 1 1
Title ""
Date ""
Rev ""
Comp ""
Comment1 ""
Comment2 ""
Comment3 ""
Comment4 ""
$EndDescr
$Comp
L eric_parts:ESP32-Minikit U1
U 1 1 5D23EF76
P 2550 4350
F 0 "U1" H 2625 5725 50  0000 C CNN
F 1 "ESP32-Minikit" H 2625 5634 50  0000 C CNN
F 2 "dev:ESP32-Minikit" H 2550 4350 50  0001 C CNN
F 3 "" H 2550 4350 50  0001 C CNN
	1    2550 4350
	1    0    0    -1  
$EndComp
$Comp
L eric_parts:ESP32-Minikit U1
U 2 1 5D240D49
P 2550 5750
F 0 "U1" H 2625 7125 50  0000 C CNN
F 1 "ESP32-Minikit" H 2625 7034 50  0000 C CNN
F 2 "dev:ESP32-Minikit" H 2550 5750 50  0001 C CNN
F 3 "" H 2550 5750 50  0001 C CNN
	2    2550 5750
	1    0    0    -1  
$EndComp
Wire Wire Line
	2200 5300 1900 5300
Wire Wire Line
	1900 5300 1900 5150
$Comp
L power:VCC #PWR03
U 1 1 5D244D02
P 1900 5150
F 0 "#PWR03" H 1900 5000 50  0001 C CNN
F 1 "VCC" H 1917 5323 50  0000 C CNN
F 2 "" H 1900 5150 50  0001 C CNN
F 3 "" H 1900 5150 50  0001 C CNN
	1    1900 5150
	1    0    0    -1  
$EndComp
$Comp
L power:VCC #PWR06
U 1 1 5D2450D6
P 2600 1900
F 0 "#PWR06" H 2600 1750 50  0001 C CNN
F 1 "VCC" H 2600 2050 50  0000 C CNN
F 2 "" H 2600 1900 50  0001 C CNN
F 3 "" H 2600 1900 50  0001 C CNN
	1    2600 1900
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR07
U 1 1 5D246D18
P 2600 2500
F 0 "#PWR07" H 2600 2250 50  0001 C CNN
F 1 "GND" H 2605 2327 50  0000 C CNN
F 2 "" H 2600 2500 50  0001 C CNN
F 3 "" H 2600 2500 50  0001 C CNN
	1    2600 2500
	1    0    0    -1  
$EndComp
$Comp
L Connector_Generic:Conn_01x02 J2
U 1 1 5D247F61
P 3900 2100
F 0 "J2" H 3980 2092 50  0000 L CNN
F 1 "Conn_01x02" H 3980 2001 50  0000 L CNN
F 2 "Connectors:PINHEAD1-2" H 3900 2100 50  0001 C CNN
F 3 "~" H 3900 2100 50  0001 C CNN
	1    3900 2100
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR010
U 1 1 5D248A1A
P 3550 2200
F 0 "#PWR010" H 3550 1950 50  0001 C CNN
F 1 "GND" H 3555 2027 50  0000 C CNN
F 2 "" H 3550 2200 50  0001 C CNN
F 3 "" H 3550 2200 50  0001 C CNN
	1    3550 2200
	1    0    0    -1  
$EndComp
Wire Wire Line
	3000 2100 3450 2100
Text Label 3200 2100 0    50   ~ 0
VBAT
$Comp
L dk_PMIC-Voltage-Regulators-Linear:MCP1700T-3302E_TT U4
U 1 1 5D250214
P 4800 1350
F 0 "U4" H 4800 1637 60  0000 C CNN
F 1 "MCP1700T-3302E_TT" H 4800 1531 60  0000 C CNN
F 2 "digikey-footprints:SOT-23-3" H 5000 1550 60  0001 L CNN
F 3 "http://www.microchip.com/mymicrochip/filehandler.aspx?ddocname=en011779" H 5000 1650 60  0001 L CNN
F 4 "MCP1700T3302ETTCT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
F 5 "MCP1700T-3302E/TT" H 5000 1850 60  0001 L CNN "MPN"
F 6 "Integrated Circuits (ICs)" H 5000 1950 60  0001 L CNN "Category"
F 7 "PMIC - Voltage Regulators - Linear" H 5000 2050 60  0001 L CNN "Family"
F 8 "http://www.microchip.com/mymicrochip/filehandler.aspx?ddocname=en011779" H 5000 2150 60  0001 L CNN "DK_Datasheet_Link"
F 9 "/product-detail/en/microchip-technology/MCP1700T-3302E-TT/MCP1700T3302ETTCT-ND/652677" H 5000 2250 60  0001 L CNN "DK_Detail_Page"
F 10 "IC REG LINEAR 3.3V 250MA SOT23-3" H 5000 2350 60  0001 L CNN "Description"
F 11 "Microchip Technology" H 5000 2450 60  0001 L CNN "Manufacturer"
F 12 "Active" H 5000 2550 60  0001 L CNN "Status"
	1    4800 1350
	1    0    0    -1  
$EndComp
Wire Wire Line
	4150 1350 4400 1350
Connection ~ 3450 2100
$Comp
L power:GND #PWR014
U 1 1 5D255562
P 4800 1650
F 0 "#PWR014" H 4800 1400 50  0001 C CNN
F 1 "GND" H 4805 1477 50  0000 C CNN
F 2 "" H 4800 1650 50  0001 C CNN
F 3 "" H 4800 1650 50  0001 C CNN
	1    4800 1650
	1    0    0    -1  
$EndComp
$Comp
L power:VCC #PWR012
U 1 1 5D257F76
P 3950 700
F 0 "#PWR012" H 3950 550 50  0001 C CNN
F 1 "VCC" H 3950 850 50  0000 C CNN
F 2 "" H 3950 700 50  0001 C CNN
F 3 "" H 3950 700 50  0001 C CNN
	1    3950 700 
	1    0    0    -1  
$EndComp
Wire Wire Line
	3450 1350 3750 1350
Wire Wire Line
	3450 1350 3450 2100
$Comp
L power:GND #PWR09
U 1 1 5D25C502
P 3450 1050
F 0 "#PWR09" H 3450 800 50  0001 C CNN
F 1 "GND" H 3455 877 50  0000 C CNN
F 2 "" H 3450 1050 50  0001 C CNN
F 3 "" H 3450 1050 50  0001 C CNN
	1    3450 1050
	1    0    0    -1  
$EndComp
Wire Wire Line
	3450 1050 3450 950 
Text Label 5100 1350 0    50   ~ 0
3v3_Backup
Text Label 3050 3900 0    50   ~ 0
3v3_Backup
$Comp
L Device:R R5
U 1 1 5D25D17A
P 2000 2450
F 0 "R5" H 2070 2496 50  0000 L CNN
F 1 "2k" H 2070 2405 50  0000 L CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 1930 2450 50  0001 C CNN
F 3 "~" H 2000 2450 50  0001 C CNN
	1    2000 2450
	1    0    0    -1  
$EndComp
Wire Wire Line
	2000 2300 2200 2300
$Comp
L power:GND #PWR05
U 1 1 5D25DD3E
P 2000 2600
F 0 "#PWR05" H 2000 2350 50  0001 C CNN
F 1 "GND" H 2005 2427 50  0000 C CNN
F 2 "" H 2000 2600 50  0001 C CNN
F 3 "" H 2000 2600 50  0001 C CNN
	1    2000 2600
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR04
U 1 1 5D25E1D0
P 1950 3200
F 0 "#PWR04" H 1950 2950 50  0001 C CNN
F 1 "GND" H 1955 3027 50  0000 C CNN
F 2 "" H 1950 3200 50  0001 C CNN
F 3 "" H 1950 3200 50  0001 C CNN
	1    1950 3200
	1    0    0    -1  
$EndComp
Wire Wire Line
	1950 3200 2200 3200
$Comp
L power:GND #PWR08
U 1 1 5D25E937
P 3400 4600
F 0 "#PWR08" H 3400 4350 50  0001 C CNN
F 1 "GND" H 3405 4427 50  0000 C CNN
F 2 "" H 3400 4600 50  0001 C CNN
F 3 "" H 3400 4600 50  0001 C CNN
	1    3400 4600
	1    0    0    -1  
$EndComp
Wire Wire Line
	3400 4600 3050 4600
$Comp
L dk_Transistors-Bipolar-BJT-Single:BC807-40_215 Q2
U 1 1 5D261943
P 3950 1250
F 0 "Q2" V 4191 1250 60  0000 C CNN
F 1 "BC807-40_215" V 4297 1250 60  0000 C CNN
F 2 "digikey-footprints:SOT-23-3" H 4150 1450 60  0001 L CNN
F 3 "https://assets.nexperia.com/documents/data-sheet/BC807_BC807W_BC327.pdf" H 4150 1550 60  0001 L CNN
F 4 "1727-2917-1-ND" H 4150 1650 60  0001 L CNN "Digi-Key_PN"
F 5 "BC807-40,215" H 4150 1750 60  0001 L CNN "MPN"
F 6 "Discrete Semiconductor Products" H 4150 1850 60  0001 L CNN "Category"
F 7 "Transistors - Bipolar (BJT) - Single" H 4150 1950 60  0001 L CNN "Family"
F 8 "https://assets.nexperia.com/documents/data-sheet/BC807_BC807W_BC327.pdf" H 4150 2050 60  0001 L CNN "DK_Datasheet_Link"
F 9 "/product-detail/en/nexperia-usa-inc/BC807-40,215/1727-2917-1-ND/763456" H 4150 2150 60  0001 L CNN "DK_Detail_Page"
F 10 "TRANS PNP 45V 0.5A SOT23" H 4150 2250 60  0001 L CNN "Description"
F 11 "Nexperia USA Inc." H 4150 2350 60  0001 L CNN "Manufacturer"
F 12 "Active" H 4150 2450 60  0001 L CNN "Status"
	1    3950 1250
	0    -1   1    0   
$EndComp
$Comp
L Device:R R6
U 1 1 5D26699C
P 3600 950
F 0 "R6" V 3393 950 50  0000 C CNN
F 1 "1k" V 3484 950 50  0000 C CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 3530 950 50  0001 C CNN
F 3 "~" H 3600 950 50  0001 C CNN
F 4 "RNCP0805FTD1K00CT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    3600 950 
	0    1    1    0   
$EndComp
Wire Wire Line
	3950 1050 3950 950 
Wire Wire Line
	3950 950  3750 950 
Wire Wire Line
	3950 700  3950 950 
Connection ~ 3950 950 
$Comp
L eric_parts:Nokia_5110_Breakout U3
U 1 1 5D27E527
P 3600 6500
F 0 "U3" H 3642 6535 50  0000 C CNN
F 1 "Nokia_5110_Breakout" H 3642 6626 50  0000 C CNN
F 2 "dev:Nokia_5110_Breakout" H 3600 6500 50  0001 C CNN
F 3 "" H 3600 6500 50  0001 C CNN
	1    3600 6500
	0    -1   1    0   
$EndComp
$Comp
L power:GND #PWR011
U 1 1 5D281653
P 3600 5800
F 0 "#PWR011" H 3600 5550 50  0001 C CNN
F 1 "GND" H 3605 5627 50  0000 C CNN
F 2 "" H 3600 5800 50  0001 C CNN
F 3 "" H 3600 5800 50  0001 C CNN
	1    3600 5800
	1    0    0    -1  
$EndComp
Wire Wire Line
	3350 6300 3350 6100
Wire Wire Line
	3600 5800 3250 5800
Wire Wire Line
	3250 5800 3250 6300
Wire Wire Line
	3050 5200 3150 5200
Wire Wire Line
	3050 5100 3250 5100
Wire Wire Line
	3250 5100 3250 5750
Wire Wire Line
	3250 5750 3050 5750
Wire Wire Line
	3050 5750 3050 6300
Wire Wire Line
	2200 5400 1900 5400
Wire Wire Line
	1900 5400 1900 6050
Wire Wire Line
	1900 6050 3150 6050
Wire Wire Line
	3150 6050 3150 6300
Wire Wire Line
	2950 6300 2950 5700
Wire Wire Line
	2950 5700 3150 5700
Wire Wire Line
	3150 5700 3150 5200
Wire Wire Line
	3050 4000 3850 4000
Wire Wire Line
	3850 4000 3850 5650
Wire Wire Line
	3850 5650 2850 5650
Wire Wire Line
	2850 5650 2850 6300
Wire Wire Line
	2200 3800 1650 3800
Wire Wire Line
	1650 3800 1650 6150
Wire Wire Line
	1650 6150 2750 6150
Wire Wire Line
	2750 6150 2750 6300
$Comp
L dk_Transistors-Bipolar-BJT-Single:BC807-40_215 Q1
U 1 1 5D288922
P 1150 6150
F 0 "Q1" V 1391 6150 60  0000 C CNN
F 1 "BC807-40_215" V 1497 6150 60  0000 C CNN
F 2 "digikey-footprints:SOT-23-3" H 1350 6350 60  0001 L CNN
F 3 "https://assets.nexperia.com/documents/data-sheet/BC807_BC807W_BC327.pdf" H 1350 6450 60  0001 L CNN
F 4 "1727-2917-1-ND" H 1350 6550 60  0001 L CNN "Digi-Key_PN"
F 5 "BC807-40,215" H 1350 6650 60  0001 L CNN "MPN"
F 6 "Discrete Semiconductor Products" H 1350 6750 60  0001 L CNN "Category"
F 7 "Transistors - Bipolar (BJT) - Single" H 1350 6850 60  0001 L CNN "Family"
F 8 "https://assets.nexperia.com/documents/data-sheet/BC807_BC807W_BC327.pdf" H 1350 6950 60  0001 L CNN "DK_Datasheet_Link"
F 9 "/product-detail/en/nexperia-usa-inc/BC807-40,215/1727-2917-1-ND/763456" H 1350 7050 60  0001 L CNN "DK_Detail_Page"
F 10 "TRANS PNP 45V 0.5A SOT23" H 1350 7150 60  0001 L CNN "Description"
F 11 "Nexperia USA Inc." H 1350 7250 60  0001 L CNN "Manufacturer"
F 12 "Active" H 1350 7350 60  0001 L CNN "Status"
	1    1150 6150
	-1   0    0    -1  
$EndComp
Wire Wire Line
	1050 5800 1050 5950
$Comp
L Device:R R4
U 1 1 5D28C5B9
P 1500 6450
F 0 "R4" V 1293 6450 50  0000 C CNN
F 1 "330" V 1384 6450 50  0000 C CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 1430 6450 50  0001 C CNN
F 3 "~" H 1500 6450 50  0001 C CNN
F 4 "A129743CT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    1500 6450
	0    1    1    0   
$EndComp
Wire Wire Line
	1650 6450 2450 6450
Wire Wire Line
	2450 6450 2450 6300
Wire Wire Line
	2450 6300 2650 6300
Wire Wire Line
	1350 6450 1050 6450
Wire Wire Line
	1050 6450 1050 6350
Wire Wire Line
	1350 6150 1350 5750
Wire Wire Line
	1350 4800 2200 4800
$Comp
L Device:R R3
U 1 1 5D28FB27
P 1350 5600
F 0 "R3" H 1280 5554 50  0000 R CNN
F 1 "1k" H 1280 5645 50  0000 R CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 1280 5600 50  0001 C CNN
F 3 "~" H 1350 5600 50  0001 C CNN
F 4 "RNCP0805FTD1K00CT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    1350 5600
	-1   0    0    1   
$EndComp
Wire Wire Line
	1350 5450 1350 4800
Text Label 1150 3050 0    50   ~ 0
VBAT
$Comp
L Device:R R1
U 1 1 5D2913A6
P 1150 3200
F 0 "R1" H 1080 3154 50  0000 R CNN
F 1 "100k" H 1080 3245 50  0000 R CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 1080 3200 50  0001 C CNN
F 3 "~" H 1150 3200 50  0001 C CNN
F 4 "RMCF0805FT100KCT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    1150 3200
	-1   0    0    1   
$EndComp
$Comp
L Device:R R2
U 1 1 5D292072
P 1150 3650
F 0 "R2" H 1080 3604 50  0000 R CNN
F 1 "100k" H 1080 3695 50  0000 R CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 1080 3650 50  0001 C CNN
F 3 "~" H 1150 3650 50  0001 C CNN
F 4 "RMCF0805FT100KCT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    1150 3650
	-1   0    0    1   
$EndComp
$Comp
L power:GND #PWR02
U 1 1 5D292341
P 1150 3800
F 0 "#PWR02" H 1150 3550 50  0001 C CNN
F 1 "GND" H 1155 3627 50  0000 C CNN
F 2 "" H 1150 3800 50  0001 C CNN
F 3 "" H 1150 3800 50  0001 C CNN
	1    1150 3800
	1    0    0    -1  
$EndComp
Wire Wire Line
	1150 3350 1150 3500
Wire Wire Line
	850  3500 1150 3500
Connection ~ 1150 3500
Wire Wire Line
	850  3800 1150 3800
Connection ~ 1150 3800
$Comp
L Device:C C2
U 1 1 5D296A04
P 4400 1750
F 0 "C2" H 4515 1796 50  0000 L CNN
F 1 "1uF" H 4515 1705 50  0000 L CNN
F 2 "Capacitors_SMD:C_0805_HandSoldering" H 4438 1600 50  0001 C CNN
F 3 "~" H 4400 1750 50  0001 C CNN
F 4 "1276-1066-1-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    4400 1750
	1    0    0    -1  
$EndComp
Wire Wire Line
	4400 1600 4400 1350
Connection ~ 4400 1350
Wire Wire Line
	4400 1350 4500 1350
$Comp
L power:GND #PWR013
U 1 1 5D297C58
P 4400 1900
F 0 "#PWR013" H 4400 1650 50  0001 C CNN
F 1 "GND" H 4405 1727 50  0000 C CNN
F 2 "" H 4400 1900 50  0001 C CNN
F 3 "" H 4400 1900 50  0001 C CNN
	1    4400 1900
	1    0    0    -1  
$EndComp
$Comp
L Device:C C1
U 1 1 5D29827A
P 850 3650
F 0 "C1" H 965 3696 50  0000 L CNN
F 1 "1uF" H 965 3605 50  0000 L CNN
F 2 "Capacitors_SMD:C_0805_HandSoldering" H 888 3500 50  0001 C CNN
F 3 "~" H 850 3650 50  0001 C CNN
F 4 "1276-1066-1-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    850  3650
	1    0    0    -1  
$EndComp
Wire Wire Line
	3700 2200 3550 2200
$Comp
L Connector_Generic:Conn_01x02 J1
U 1 1 5D29AF4D
P 3600 1900
F 0 "J1" V 3700 1800 50  0000 L CNN
F 1 "Conn_01x02" V 3600 1300 50  0000 L CNN
F 2 "Connectors:PINHEAD1-2" H 3600 1900 50  0001 C CNN
F 3 "~" H 3600 1900 50  0001 C CNN
	1    3600 1900
	0    -1   -1   0   
$EndComp
Wire Wire Line
	3450 2100 3600 2100
Text Label 3350 6100 0    50   ~ 0
3v3_Backup
Wire Wire Line
	1150 3500 2200 3500
Text Label 1050 5800 1    50   ~ 0
3v3_Backup
Text Label 3000 2300 0    50   ~ 0
CHG_BATT
Text Label 2200 3600 2    50   ~ 0
CHG_BATT
Text Label 3900 3150 0    50   ~ 0
CHG_BATT
$Comp
L Device:R R7
U 1 1 5D313F0F
P 3900 2900
F 0 "R7" H 3970 2946 50  0000 L CNN
F 1 "100k" H 3970 2855 50  0000 L CNN
F 2 "Resistors_SMD:R_0805_HandSoldering" V 3830 2900 50  0001 C CNN
F 3 "~" H 3900 2900 50  0001 C CNN
F 4 "RMCF0805FT100KCT-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    3900 2900
	1    0    0    -1  
$EndComp
Wire Wire Line
	3900 3050 3900 3150
Text Label 3900 2750 0    50   ~ 0
3v3_Backup
$Comp
L Device:C C3
U 1 1 5D315F2E
P 3500 2850
F 0 "C3" H 3615 2896 50  0000 L CNN
F 1 "4.7uF" H 3615 2805 50  0000 L CNN
F 2 "Capacitors_SMD:C_0805_HandSoldering" H 3538 2700 50  0001 C CNN
F 3 "~" H 3500 2850 50  0001 C CNN
F 4 "732-7623-1-ND" H 5000 1750 60  0001 L CNN "Digi-Key_PN"
	1    3500 2850
	1    0    0    -1  
$EndComp
$Comp
L power:GND #PWR01
U 1 1 5D31696D
P 3500 3000
F 0 "#PWR01" H 3500 2750 50  0001 C CNN
F 1 "GND" H 3505 2827 50  0000 C CNN
F 2 "" H 3500 3000 50  0001 C CNN
F 3 "" H 3500 3000 50  0001 C CNN
	1    3500 3000
	1    0    0    -1  
$EndComp
Text Label 3500 2700 0    50   ~ 0
VBAT
$Comp
L Battery_Management:MCP73831-2-OT U2
U 1 1 5D243F8C
P 2600 2200
F 0 "U2" H 2950 1950 50  0000 C CNN
F 1 "MCP73831-2-OT" H 2950 1850 50  0000 C CNN
F 2 "TO_SOT_Packages_SMD:SOT-23-5_HandSoldering" H 2650 1950 50  0001 L CIN
F 3 "http://ww1.microchip.com/downloads/en/DeviceDoc/20001984g.pdf" H 2450 2150 50  0001 C CNN
	1    2600 2200
	1    0    0    -1  
$EndComp
$EndSCHEMATC
