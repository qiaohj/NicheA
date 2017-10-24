##librarys: ggplot2, plyr, scales
##parameters: @DataFile, @Variable, @ResultName,@Target
setwd("@Target")
library("ggplot2")
library("plyr")
library("scales") 

d<-read.csv(file="@DataFile", header = TRUE, sep = ",", quote="\"", dec=".",fill = TRUE)

d_data<-d[which(d$Range!='nodata'),]
d_data$Range<-as.numeric(as.character(d_data$Range))
d_data<-d_data[which(d_data$Total_count!=0),]


d_t <- data.frame(Range=d_data$Range, Value=d_data$Occu_count, G='Occurrences')
d_t <- rbind(d_t, data.frame(Range=d_data$Range, Value=d_data$Enm_count, G='ENM'))

d_t_all <- rbind(d_t, data.frame(Range=d_data$Range, Value=d_data$Total_count, G='Background'))

max_value <- lapply(split(d_t, d_t$Range), function(x) {
    x[which.max(x$Value),]
  }
)

min_value <- lapply(split(d_t, d_t$Range), function(x) {
  x[which.min(x$Value),]
}
)


max_value<-do.call(rbind, max_value)
min_value<-do.call(rbind, min_value)

d_t<-merge(d_t, max_value, by="Range")
d_t<-merge(d_t, min_value, by="Range")

drops <- c("G.y","G")
d_t<-d_t[,!(names(d_t) %in% drops)]
colnames(d_t)<-c("Range", "Value", "G", "MAX", "MIN")


p <- ggplot(d_t_all, aes(x=Range, y=Value, group=G, colour=factor(G), fill='Experimental Condition'))
p <- p + geom_line()
p <- p + scale_y_continuous(name="Count")
p <- p + scale_x_continuous(name="@Variable Range")
p <- p + scale_colour_discrete(name  ="Groups")
ggsave(plot = p, filename = "@ResultName.all.png")

p <- ggplot(d_t_all, aes(x=Range, y=Value, group=G, colour=factor(G), fill='Experimental Condition'))
p <- p + geom_line()
p <- p + scale_y_continuous(name="Count (Log 10 transformed)", trans=log10_trans())
p <- p + scale_x_continuous(name="@Variable Range")
p <- p + scale_colour_discrete(name  ="Groups")
ggsave(plot = p, filename = "@ResultName.all.log.png")

p <- ggplot(d_t, 
            aes(x=Range, y=Value, group=G, colour=factor(G)))
p <- p + geom_ribbon(aes(ymin=MIN,ymax=MAX),color="yellow",alpha=0.2)
p <- p + geom_line()
p <- p + scale_y_continuous(name="Count")
p <- p + scale_x_continuous(name="@Variable Range")
p <- p + scale_colour_discrete(name  ="Groups")
ggsave(plot = p, filename = "@ResultName.shadow.png")


