
dailyCrunch <- function(cData, var, cProbs = c(0.1, 0.5, 0.9)){

  cDataA <- aggregate.data.frame(var / cPop, by = list(day = cData$day), function(x) quantile(x, probs = cProbs), simplify = F)
  cDataA$li <- unlist(cDataA$x)[names(unlist(cDataA$x)) == paste0(as.character(cProbs[1] * 100), "%")]
  cDataA$mi <- unlist(cDataA$x)[names(unlist(cDataA$x)) == paste0(as.character(cProbs[2] * 100), "%")]
  cDataA$ui <- unlist(cDataA$x)[names(unlist(cDataA$x)) == paste0(as.character(cProbs[3] * 100), "%")]
return(cDataA)
}
