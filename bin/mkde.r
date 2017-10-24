setwd("@Target")
require(hypervolume)
require(geometry)

IN_HYPER<-function(samples, all_samples){
  hv = hypervolume(samples, reps=10000, 
                   bandwidth=estimate_bandwidth(samples), quantile=0)
  result=logical(length(all_samples[,1]))
  in_hyper=hypervolume_inclusion_test(hv, points=all_samples, reduction_factor=0.5)
  for (i in 1:length(in_hyper)){
    if (in_hyper[i]){
      result[i] = TRUE
    }else{
      result[i] = FALSE
    }
  }
  return(result)
}

samples<-read.table("@Filename", head=T, sep=",")
all_samples<-read.table("all.txt", head=T, sep=",")
samples[,c(3:5)]
result<-IN_HYPER(samples[,c(3:5)], all_samples[,c(3:5)])
result_a<-all_samples
result_a$result<-result
write.table(result_a, file="@Result", row.names=F, sep=",")