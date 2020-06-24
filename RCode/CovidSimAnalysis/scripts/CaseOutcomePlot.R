library(ggplot2)
library(reshape2)

ages <- 0:100
outcomeDF <- data.frame(ages)
names(outcomeDF) <- "Age"

outcomeDF$Asymptomatic <- 0.5
outcomeDF$Asymptomatic[outcomeDF$Age <= 20] <- 0.79
outcomeDF$Asymptomatic[outcomeDF$Age >= 70] <- 0.31

outcomeDF$Moderate <- 1 - outcomeDF$Asymptomatic
outcomeDF$Moderate[outcomeDF$Age < 18] <- outcomeDF$Moderate[outcomeDF$Age < 18] * 0.99
outcomeDF$Moderate[outcomeDF$Age >= 18 & outcomeDF$Age < 65] <- outcomeDF$Moderate[outcomeDF$Age >= 18 & outcomeDF$Age < 65] * 0.8
outcomeDF$Moderate[outcomeDF$Age >= 65] <- outcomeDF$Moderate[outcomeDF$Age >= 65] * 0.2

outcomeDF$Severe <- 1 - outcomeDF$Asymptomatic
outcomeDF$Severe[outcomeDF$Age < 18] <- outcomeDF$Severe[outcomeDF$Age < 18] * 0.01
outcomeDF$Severe[outcomeDF$Age >= 18 & outcomeDF$Age < 65] <- outcomeDF$Severe[outcomeDF$Age >= 18 & outcomeDF$Age < 65] * 0.2
outcomeDF$Severe[outcomeDF$Age >= 65] <- outcomeDF$Severe[outcomeDF$Age >= 65] * 0.8

outcomeDF$Death <- outcomeDF$Severe
outcomeDF$Death <- outcomeDF$Death * 0.25 * ((outcomeDF$Age / 85) ^ 2)

outcomeDF$Severe <- outcomeDF$Severe - outcomeDF$Death

outcomeDFMelt <- melt(outcomeDF, id.vars = 1)
caseOutcomes <- ggplot(outcomeDFMelt, aes(x = Age, y = value, fill = variable)) + geom_bar(stat = "identity") + ylab("Proportion of cases") + theme(text = element_text(size = 16))
       