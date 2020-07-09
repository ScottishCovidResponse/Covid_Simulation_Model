deathData <- import("deathData/NRSDeathsWK23.xlsx")
hbDeaths <- deathData[deathData$HB == healthboard,]
hbDeaths <- cumsum(hbDeaths[-1])
deathDays <- seq(from = 0, length.out = (ncol(deathData) - 1), by = 7)
deathdf <- data.frame(deathDays)
deathdf$cumDeaths <- cumsum(as.vector(unlist(hbDeaths)))

deathDataCumSum <- apply(deathData[,2:ncol(deathData)], 1, function(x) cumsum(x))

hospitalised <- read.csv("deathData/NHS_HealthBoard_Data.csv")
hospitalised$Date <- hospitalised$Date - min(hospitalised$Date) + 58
#hospitalised$Date <- as.Date(hospitalised$Date, origin = "1900-01-01")
hospitalised <- subset(hospitalised, select = c("Date", "NHS.Lothian"))
