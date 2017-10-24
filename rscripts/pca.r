##librarys: ggplot2,grid
##parameters: @Target
setwd("@Target")
library("ggplot2")
library("grid") 
d<-read.csv(file="@Target/proportion_of_variances.csv", 
  header = T, sep = ",", quote="\"", dec=".",fill = TRUE)
d$Proportion <- as.numeric(as.character(d$Proportion))
d$PC<-factor(d$PC, as.character(d$PC))
p <- ggplot(d, aes(x=PC, y=Proportion, group=1))
p <- p + geom_bar(fill="#DD8888", stat="identity")
p <- p + geom_line(colour="red", size=1.5)
p <- p + geom_point(colour="red", size=4, shape=21, fill="white")
ggsave(plot = p, filename = "@Target/proportion.png")

PC_min_max<-read.table(
  file="@Target/pc_min_max_values.csv", 
  header = T, sep = ",", quote="\"", dec=".", colClasses="numeric")

PC_rotation<-read.table(
  file="@Target/eigenvectors.csv", 
  header = T, sep = ",", quote="\"", dec=".", row.names=1, 
  colClasses=c("character", rep("numeric", dim(PC_min_max)[2])))

datapc <- data.frame(varnames=rownames(PC_rotation), PC_rotation)
x="PC.1"
y="PC.2"
mult <- min(
  ((PC_min_max[2,y] -PC_min_max[1,y])/(max(datapc[,y])-min(datapc[,y]))),
  ((PC_min_max[2,x] -PC_min_max[1,x])/(max(datapc[,x])-min(datapc[,x])))
)
datapc <- transform(datapc,
                    v1 = .7 * mult * (get(x)),
                    v2 = .7 * mult * (get(y))
)

colors <- c("black", "black", "red", "red")
plot <- ggplot(datapc, aes_string(x=x, y=y))

plot <- plot + coord_equal() + geom_text(data=datapc, aes(x=v1, y=v2, label=varnames), size = 5, vjust=1, color=colors[3])
plot <- plot + geom_segment(data=datapc, aes(x=0, y=0, xend=v1, yend=v2), arrow=arrow(length=unit(0.2,"cm")), alpha=0.75, color=colors[4])
ggsave(plot = plot, filename = "@Target/biplot.png")

