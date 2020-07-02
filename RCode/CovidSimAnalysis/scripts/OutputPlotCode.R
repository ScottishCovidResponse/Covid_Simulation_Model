library(ggplot2)
library(reshape2)
library(ggpubr)
library(dplyr)
library(rio)

healthboard <- "Lothian"

datafile <- "exampledata/Lothian/out_25617390_20200702.csv"

cPop <- 910000
lockdown <- 55
plotFile <- "Plots/Lothian"

if(!dir.exists(plotFile)) dir.create(plotFile)

baseline <- read.csv(datafile)

source("scripts/SourceScripts/Data munching.R")
source("scripts/SourceScripts/DetahMunching.R")
source("scripts/CaseOutcomePlot.R")

ggsave(paste(plotFile, "CaseOutcomes.jpg", sep = "/"), caseOutcomes, height = 6, width = 6, units = "in")

baseline$Infected <- baseline$L + baseline$A + baseline$P1 + baseline$P2

nIter <- max(baseline$iter) + 1

preLockdown <- baseline[baseline$day < lockdown,]

# Basic plot of numbers infected

aInfections <- dailyCrunch(baseline, baseline$Infected)
basicInfectedPlot <- ggplot(data = aInfections, aes(x=day, y= mi)) +
  geom_line() +
  geom_ribbon(aes(x=day, ymin = li, ymax = ui), fill="red", alpha = .15) + 
  geom_vline(xintercept = lockdown) + 
  ylab("Proportion with active infection") + 
  xlab("Day") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "ActiveInfections.jpg", sep = "/"), basicInfectedPlot, height = 6, width = 6, units = "in")


rInfections <- dailyCrunch(baseline, baseline$R)
basicRecoveredPlot <- ggplot(data = rInfections, aes(x=day, y= mi)) +
  geom_line() +
  geom_ribbon(aes(x=day, ymin = li, ymax = ui), fill="red", alpha = .15) + geom_vline(xintercept = lockdown) + ylab("Proportion recovered") + xlab("Day") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "RecoveredInfections.jpg", sep = "/"), basicRecoveredPlot, height = 6, width = 6, units = "in")


allInfected <- dailyCrunch(baseline, baseline$R + baseline$L + baseline$A + baseline$P1 + baseline$P2 + baseline$D)

basicInfectedAllPlot <- ggplot(data = allInfected, aes(x=day, y= mi)) +
  geom_line() +
  geom_ribbon(aes(x=day, ymin = li, ymax = ui), fill="red", alpha = .15) + geom_vline(xintercept = lockdown) + ylab("Proportion infected") + xlab("Day") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "AllInfected.jpg", sep = "/"), basicInfectedAllPlot, height = 6, width = 6, units = "in")


#### Molten data frame
baselineI <- aggregate.data.frame(baseline[,3:40] / cPop, by = list(day = baseline$day), median)
baselineIMelt <- melt(baselineI, id.vars = "day")

baselineIMelt$Infection[baselineIMelt$variable == "L"] <- "Latent"
baselineIMelt$Infection[baselineIMelt$variable == "A"] <- "Asymptomatic"
baselineIMelt$Infection[baselineIMelt$variable == "P1"] <- "Mild"
baselineIMelt$Infection[baselineIMelt$variable == "P2"] <- "Severe"
baselineIMelt$Infection[baselineIMelt$variable == "D"] <- "Dead"
baselineIMelt$Infection <- as.factor(baselineIMelt$Infection)

allInfPlot <- ggplot(baselineIMelt[baselineIMelt$variable %in% c("L", "A", "P1", "P2", "D"),], aes(x = day, y = value, color = Infection)) +  
  geom_line() + 
  ylab("Proportion of population infected") + 
  xlab("Day") + 
  theme(legend.position = "right", text = element_text(size = 16), legend.margin=margin()) + 
  geom_vline(xintercept = lockdown) +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "InfectedType.jpg", sep = "/"), allInfPlot, height = 6, width = 6, units = "in")


# Site of infection v2 ----------------------------------------------------

baselineSOIMelt <- melt(baseline, id.vars = c("iter", "day"))
baselineSOIMelt$Status <- factor(NA, levels = c("Home", "Pupil", "Resident", "Transport", "Worker", "Visitor"))
baselineSOIMelt$Status[grepl("IHome_I", baselineSOIMelt$variable)] <- "Home"
baselineSOIMelt$Status[grepl("_W", baselineSOIMelt$variable)] <- "Worker"
baselineSOIMelt$Status[grepl("_V", baselineSOIMelt$variable)] <- "Visitor"
baselineSOIMelt$Status[grepl("_R", baselineSOIMelt$variable)] <- "Resident"
baselineSOIMelt$Status[grepl("INur_V", baselineSOIMelt$variable)] <- "Pupil"
baselineSOIMelt$Status[grepl("ISch_V", baselineSOIMelt$variable)] <- "Pupil"
baselineSOIMelt$Status[grepl("Transport", baselineSOIMelt$variable)] <- "Transport"

baselineSOIRed <- baselineSOIMelt[!is.na(baselineSOIMelt$Status),]
baselineSOIRedAgg <- aggregate(baselineSOIRed$value, by = list("iter" = baselineSOIRed$iter, "day" = baselineSOIRed$day, "Status" = baselineSOIRed$Status), sum)

baselineSOIRedAgg <- aggregate.data.frame(baselineSOIRedAgg$x / cPop, by = list(day = baselineSOIRedAgg$day, Status = baselineSOIRedAgg$Status), function(x) quantile(x, probs = c(0.1, 0.5, 0.9)), simplify = F)
baselineSOIRedAgg$r10 <- unlist(baselineSOIRedAgg$x)[names(unlist(baselineSOIRedAgg$x)) == "10%"]
baselineSOIRedAgg$r50 <- unlist(baselineSOIRedAgg$x)[names(unlist(baselineSOIRedAgg$x)) == "50%"]
baselineSOIRedAgg$r90 <- unlist(baselineSOIRedAgg$x)[names(unlist(baselineSOIRedAgg$x)) == "90%"]

SiteOfInfectionAll <- ggplot(data = baselineSOIRedAgg, aes(x=day, y= r50, colour = Status)) +
  geom_line() +
  xlab("Day") + ylab("Proportion of population infected") + theme(text = element_text(size = 16)) + geom_vline(xintercept = 55)
ggsave(paste(plotFile, "SiteOfInfectionAll.jpg", sep = "/"), SiteOfInfectionAll, height = 6, width = 6, units = "in")

SiteOfInfectionPreLock <- ggplot(data = baselineSOIRedAgg[baselineSOIRedAgg$day <= lockdown,], aes(x=day, y= r50, colour = Status)) +
  geom_line() +
  xlab("Day") + ylab("Proportion of population infected") + theme(text = element_text(size = 16)) + geom_vline(xintercept = 55)
ggsave(paste(plotFile, "SiteOfInfectionPreLock.jpg", sep = "/"), SiteOfInfectionPreLock, height = 6, width = 6, units = "in")

#### Site of infection barchart
baselineSOIAgg2 <- aggregate(baselineSOIRed$value, by = list("Site" = baselineSOIRed$variable, "Status" = baselineSOIRed$Status), sum)
baselineSOIAgg2$Site <- as.character(baselineSOIAgg2$Site)
baselineSOIAgg2$SiteR[grepl("Home", baselineSOIAgg2$Site)] <- "Home"
baselineSOIAgg2$SiteR[grepl("CHome", baselineSOIAgg2$Site)] <- "Care home"
baselineSOIAgg2$SiteR[grepl("Cs", baselineSOIAgg2$Site)] <- "Const. site"
baselineSOIAgg2$SiteR[grepl("Hos", baselineSOIAgg2$Site)] <- "Hospital"
baselineSOIAgg2$SiteR[grepl("Nur", baselineSOIAgg2$Site)] <- "Nursery"
baselineSOIAgg2$SiteR[grepl("Off", baselineSOIAgg2$Site)] <- "Office"
baselineSOIAgg2$SiteR[grepl("Res", baselineSOIAgg2$Site)] <- "Restaurant"
baselineSOIAgg2$SiteR[grepl("Sch", baselineSOIAgg2$Site)] <- "School"
baselineSOIAgg2$SiteR[grepl("Sho", baselineSOIAgg2$Site)] <- "Shop"
baselineSOIAgg2$SiteR[grepl("Transport", baselineSOIAgg2$Site)] <- "Travel"


siteOfInfectionBarAll <- ggplot(baselineSOIAgg2, aes(x = SiteR, y = x / nIter / cPop, fill = Status)) + geom_bar(stat = "identity", position = "stack") + ylab("Proportion of population infected") + xlab("Site of infection") + theme(text = element_text(size = 16), axis.text.x = element_text(angle = 45, hjust = 1))
ggsave(paste(plotFile, "SiteOfInfectionBarAll.jpg", sep = "/"), siteOfInfectionBarAll, height = 6, width = 6, units = "in")


baselineSOIRedRed <- baselineSOIRed[baselineSOIRed$day <= lockdown,]
baselineSOIAgg2 <- aggregate(baselineSOIRedRed$value, by = list("Site" = baselineSOIRedRed$variable, "Status" = baselineSOIRedRed$Status), sum)
baselineSOIAgg2$Site <- as.character(baselineSOIAgg2$Site)
baselineSOIAgg2$SiteR[grepl("Home", baselineSOIAgg2$Site)] <- "Home"
baselineSOIAgg2$SiteR[grepl("CHome", baselineSOIAgg2$Site)] <- "Care home"
baselineSOIAgg2$SiteR[grepl("Cs", baselineSOIAgg2$Site)] <- "Const. site"
baselineSOIAgg2$SiteR[grepl("Hos", baselineSOIAgg2$Site)] <- "Hospital"
baselineSOIAgg2$SiteR[grepl("Nur", baselineSOIAgg2$Site)] <- "Nursery"
baselineSOIAgg2$SiteR[grepl("Off", baselineSOIAgg2$Site)] <- "Office"
baselineSOIAgg2$SiteR[grepl("Res", baselineSOIAgg2$Site)] <- "Restaurant"
baselineSOIAgg2$SiteR[grepl("Sch", baselineSOIAgg2$Site)] <- "School"
baselineSOIAgg2$SiteR[grepl("Sho", baselineSOIAgg2$Site)] <- "Shop"
baselineSOIAgg2$SiteR[grepl("Transport", baselineSOIAgg2$Site)] <- "Travel"


siteOfInfectionBarPreLock <- ggplot(baselineSOIAgg2, aes(x = SiteR, y = x / nIter / cPop, fill = Status)) + 
  geom_bar(stat = "identity", position = "stack") + 
  ylab("Proportion of population infected") + 
  xlab("Site of infection") + 
  theme(text = element_text(size = 16), axis.text.x = element_text(angle = 45, hjust = 1))
ggsave(paste(plotFile, "SiteOfInfectionBarPreLock.jpg", sep = "/"), siteOfInfectionBarPreLock, height = 6, width = 6, units = "in")

# comb <- ggarrange(iPlot, ggarrange(SiteOfInfection, siteOfInfectionBarAll, ncol = 2, labels = c("B", "C")), nrow = 2, labels = "A")

comb <- ggarrange(ggarrange(basicInfectedPlot, allInfPlot, ncol = 2, labels = c("A", "B")), ggarrange(SiteOfInfectionAll, siteOfInfectionBarAll, ncol = 2, labels = c("C", "D"), common.legend = T, legend = "bottom"), nrow = 2)


ggsave(paste(plotFile, "CombinationInfectionPlot.jpg", sep = "/"), comb, height = 6, width = 6, units = "in")


# R0 estimation -----------------------------------------------------------
baseline$NewInfections <- baseline$IAdu + baseline$IChi + baseline$IPen + baseline$IInf + baseline$ISeed
baseline$SecInfectionsNA <- baseline$SecInfections
baseline$SecInfectionsNA[is.na(baseline$SecInfectionsNA)] <- 0
baselineAgg <- aggregate(baseline$SecInfectionsNA, by = list("iter" = baseline$iter), function(x) cumsum(x), simplify = T)

baseline <- baseline %>% group_by(iter) %>% mutate(cumSecInfect = cumsum(SecInfectionsNA))
baseline <- baseline %>% group_by(iter) %>% mutate(cumInfect = cumsum(NewInfections))

baseline <- as.data.frame(baseline)         
baseline$effR <- baseline$cumSecInfect / baseline$cumInfect

baselineRAgg <- aggregate(baseline$effR, by = list("day" = baseline$day), function(x) quantile(x, probs = c(0.1, 0.5, 0.9), na.rm = T), simplify = F )
baselineRAgg$p10 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "10%"]
baselineRAgg$p50 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "50%"]
baselineRAgg$p90 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "90%"]

# cumulative R
cumR <- ggplot(baselineRAgg, aes(x = day, y = p50)) + geom_point() + geom_errorbar(aes(ymin = p10, ymax = p90)) +
  scale_y_continuous(limits = c(0, NA)) + geom_vline(xintercept = 55) + ylab("Cumulative R") + theme(text = element_text(size = 16))
ggsave(paste(plotFile, "CumulativeR.jpg", sep = "/"), cumR, height = 6, width = 6, units = "in")



### Daily R

baseline$dR <- NA
baseline$dR[!is.na(baseline$SecInfections)] <- baseline$SecInfections[!is.na(baseline$SecInfections)] / baseline$NewInfections[!is.na(baseline$SecInfections)]

baselineRAgg <- aggregate(baseline$dR, by = list("day" = baseline$day), function(x) quantile(x, probs = c(0.1, 0.5, 0.9), na.rm = T), simplify = F)
baselineRAgg$p10 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "10%"]
baselineRAgg$p50 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "50%"]
baselineRAgg$p90 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "90%"]

# Daily R
dailyR <- ggplot(baselineRAgg, aes(x = day, y = p50)) + geom_point() + geom_errorbar(aes(ymin = p10, ymax = p90)) +
  geom_vline(xintercept = 55) + ylab("Daily R") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "DailyR.jpg", sep = "/"), dailyR, height = 6, width = 6, units = "in")


# Generation time ---------------------------------------------------------

baselineRAgg <- aggregate(baseline$GenerationTime, by = list("day" = baseline$day), function(x) quantile(x, probs = c(0.1, 0.5, 0.9), na.rm = T), simplify = F)
baselineRAgg$p10 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "10%"]
baselineRAgg$p50 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "50%"]
baselineRAgg$p90 <- unlist(baselineRAgg$x)[names(unlist(baselineRAgg$x)) == "90%"]

# cumulative R
generationTime <- ggplot(baselineRAgg, aes(x = day, y = p50)) + geom_point() + geom_errorbar(aes(ymin = p10, ymax = p90)) +
  scale_y_continuous(limits = c(0, NA)) + geom_vline(xintercept = 55) + ylab("Generation time (days)") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "GenerationTime.jpg", sep = "/"), generationTime, height = 6, width = 6, units = "in")



# Seeds -------------------------------------------------------------------

seeds <- dailyCrunch(baseline, baseline$ISeed)

seedPlot <- ggplot(seeds, aes(x = day, y = mi * cPop)) + geom_point() + geom_errorbar(aes(ymin = li * cPop, ymax = ui * cPop)) +
  scale_y_continuous(limits = c(0, NA)) + geom_vline(xintercept = 55) + ylab("Number of seeds") +
  theme(text = element_text(size = 16))
ggsave(paste(plotFile, "Seeds.jpg", sep = "/"), seedPlot, height = 6, width = 6, units = "in")

# Deaths ------------------------------------------------------------------

baseline <- baseline %>% group_by(iter) %>% mutate(cumDHome = cumsum(DHome))
baseline <- baseline %>% group_by(iter) %>% mutate(cumDHosp = cumsum(DHospital))
baseline <- baseline %>% group_by(iter) %>% mutate(cumDCHome = cumsum(DCareHome))
baseline$cumDeaths <- baseline$cumDeaths <- baseline$cumDCHome + baseline$cumDHome + baseline$cumDHosp

deathCum <- dailyCrunch(baseline, baseline$cumDeaths)

deathCumPlot <- ggplot(data = deathCum, aes(x=day, y= mi * cPop)) +
  geom_line() +
  geom_ribbon(aes(x=day, ymin = li * cPop, ymax = ui * cPop), fill="red", alpha = .15) + geom_vline(xintercept = lockdown) + ylab("Total deaths") + xlab("Day") + geom_point(data = deathdf[deathdf$deathDays > 28,], mapping = aes(x = deathDays - 28, y = cumDeaths)) +
  theme(text = element_text(size = 16))

ggsave(paste(plotFile, "DeathCumPlot.jpg", sep = "/"), deathCumPlot, height = 6, width = 6, units = "in")


# Hospitalisations --------------------------------------------------------

deathCum <- dailyCrunch(baseline, baseline$HospitalisedToday)

hospitalCurrentPlot <- ggplot(data = deathCum, aes(x=day, y= mi * cPop)) +
  geom_line() +
  geom_ribbon(aes(x=day, ymin = li * cPop, ymax = ui * cPop), fill="red", alpha = .15) + geom_vline(xintercept = lockdown) + ylab("Total hospitalised per day") + xlab("Day") + # geom_point(data = deathdf[deathdf$deathDays > 28,], mapping = aes(x = deathDays - 28, y = cumDeaths)) +
  theme(text = element_text(size = 16))

ggsave(paste(plotFile, "HospitalisedPerDay.jpg", sep = "/"), hospitalCurrentPlot, height = 6, width = 6, units = "in")


deathPlace <- baselineIMelt[baselineIMelt$variable %in% c("DHome", "DHospital", "DCareHome"),]
deathPlaceAgg <- aggregate(deathPlace$value * cPop, by = list("DeathLocation" = deathPlace$variable), sum)


day150 <- baseline[baseline$day == 149,]
day85 <- baseline[baseline$day == 85,]
day55 <- baseline[baseline$day == 55,]

mean(day150$D) / mean(day150$R)
mean(day150$IAdu)
mean(day150$IPen)
mean(day85$D)

mean(day55$IPen)
mean(day55$IAdu)

sum(baseline$DPen) / nIter
sum(baseline$DAdul) / nIter
sum(baseline$DChi) / nIter

sum(baseline$DHome) / nIter
sum(baseline$DHospital) / nIter
sum(baseline$DCareHome) / nIter
