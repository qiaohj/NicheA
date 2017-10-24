setwd("@Target")
library("ggplot2")
d<-read.table("roc.@Threshold.csv", head=T, sep=",")
conf<-read.table("conf.@Threshold.csv", head=T, sep=",")
point<-read.table("points.@Threshold.csv", head=T, sep=",")
point_null<-read.table("points_null.@Threshold.csv", head=T, sep=",")
point_all<-read.table("points_all.@Threshold.csv", head=T, sep=",")
p <- ggplot(d, aes(x = ProportionOfPredictedArea, y=X1.omission))
p <- p + geom_line()
p <- p + xlab("Proportion of predicted area") +  ylab("1 - omission") +
  ggtitle("Partial ROC with E = @Threshold")
p<-p+geom_polygon(data=point_all, aes(x=x, y=y, group=group), fill="blue")
p<-p+geom_polygon(data=point, aes(x=x, y=y, group=group), fill="green")
p<-p+geom_polygon(data=point_null, aes(x=x, y=y, group=group), fill="red")

ggsave(p, filename="roc.@Threshold.png", width=5, height=5, units="in")