library("raster")
library("grid")
library("ggplot2")
library("rgdal")
#Please replace the necessary variables below

target<-"@TARGET"
write.table(c(1), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
is_transfer<-@ISTRANS

group_origin<-c(@ORILIST)
group_trans<-c(@TRANSLIST)

#before you set up the variables below, you must create the folders on your computer ahead.
pca_origin_folder<-"@ORITARGET"
pca_trans_folder<-"@TRANSTARGET"

if (!file.exists(pca_origin_folder)){
  dir.create(pca_origin_folder)
}

if (is_transfer){
  if (!file.exists(pca_trans_folder)){
    dir.create(pca_trans_folder)
  }
}
if (is_transfer){
  if (length(group_origin)!=length(group_trans)){
    print("Error! The lengths of original dataset and target dataset are different!")
  }
}
d_o<-data.frame()
d_t<-data.frame()

i=1
for (i in c(1:length(group_origin))){
write.table(c(1 + i), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
  print(paste("Reading raster file", group_origin[i]))
  r <- values(raster(group_origin[i]))
  if (i==1){
    r_standard <- r
  }
  r <- r[which(!is.na(r_standard))]
  
  if (dim(d_o)[1]==0){
    d_o<-data.frame(ID=c(1:length(r)))
  }
  d_o[,paste("V", i, sep="")] <- r  
  
  if (is_transfer){
	  print(paste("Reading raster file", group_trans[i]))
	  
	  r <- values(raster(group_trans[i]))
	  r <- r[which(!is.na(r_standard))]
	  
	  if (dim(d_t)[1]==0){
	    d_t<-data.frame(ID=c(1:length(r)))
	  }
	  d_t[,paste("V", i, sep="")] <- r
  }
} 
write.table(c(30), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)

d_t<-d_t[complete.cases(d_t),]
d_o<-d_o[complete.cases(d_o),]

print("calculating PCA")
pca <- prcomp(d_o[, c(2: dim(d_o)[2])],
                 center = T,
                 scale. = T) 
write.table(c(45), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
PCbiplot <- function(PC, x="PC1", y="PC2") {
  # PC being a prcomp object
  data <- data.frame(PC$x)
  datapc <- data.frame(varnames=rownames(PC$rotation), PC$rotation)
  mult <- min(
    (max(data[,y]) - min(data[,y])/(max(datapc[,y])-min(datapc[,y]))),
    (max(data[,x]) - min(data[,x])/(max(datapc[,x])-min(datapc[,x])))
  )
  datapc <- transform(datapc,
                      v1 = .7 * mult * (get(x)),
                      v2 = .7 * mult * (get(y))
  )
  
  write.table(PC$rotation, 
              file=paste(target,"rotation.csv", sep="/"), 
              row.names=F, sep=",")
  
  
  plot <- ggplot(datapc)
  plot <- plot + coord_equal() + geom_text(data=datapc, aes(x=v1, y=v2, label=varnames), size = 5, vjust=1, color="red")
  plot <- plot + geom_segment(data=datapc, aes(x=0, y=0, xend=v1, yend=v2), arrow=arrow(length=unit(0.2,"cm")), alpha=0.75, color="red")
  ggsave(plot, file=paste(target, "biplot.png", sep="/"), unit="mm", width=200, height=200, dpi=100)
  
  vars <- apply(PC$x, 2, var)  
  props_value <- vars / sum(vars)
  props<-data.frame(PC=attributes(props_value)$names, Proportion=props_value)
  
  write.table(props, 
              file=paste(target,"proportion_of_variances.csv", sep="/"), 
              row.names=F, sep=",")
  
  props$PC<-factor(props$PC, levels = paste("PC", seq(from=1,to=length(vars)), sep=""))
  p <- ggplot(props, aes(x=factor(PC), y=Proportion, group=1))
  p <- p + geom_bar(fill="#DD8888", stat="identity")
  p <- p + geom_line(colour="red", size=1.5)
  p <- p + geom_point(colour="red", size=4, shape=21, fill="white")
  ggsave(plot = p, filename = paste(target, "proportion.png", sep="/"), 
         unit="mm", width=200, height=200, dpi=100)
}
PCbiplot(pca)

print("Calculate original PCA")
p_d<-predict(pca, 
             newdata=d_o[, c(2: dim(d_o)[2])])

sample_raster<-raster(group_origin[1])
values(sample_raster)<-NA

write.table(c(50), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
for (i in c(1:dim(p_d)[2])){
	write.table(c(50 + i), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
  print(paste("Saving results", i, "/", dim(p_d)[2]))
  values(sample_raster)<-NA
  values(sample_raster)[which(!is.na(r_standard))]<-p_d[,i]
  writeRaster(sample_raster, filename=paste(pca_origin_folder, "/", "PC", i , ".tif", sep=""), 
              format="GTiff", overwrite=T)
}

write.table(c(60), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
if (is_transfer){

  print("Transferring to target raster")
  p_d<-predict(pca, 
          newdata=d_t[, c(2: dim(d_t)[2])])
  
  sample_raster<-raster(group_origin[1])
  values(sample_raster)<-NA
  
  for (i in c(1:dim(p_d)[2])){
  write.table(c(60 + i), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)
    print(paste("Saving results", i, "/", dim(p_d)[2]))
    values(sample_raster)<-NA
    values(sample_raster)[which(!is.na(r_standard))]<-p_d[,i]
    writeRaster(sample_raster, filename=paste(pca_trans_folder, "/", "PC", i , ".tif", sep=""), 
                format="GTiff", overwrite=T)
  }
}
write.table(c(80), file=paste(target, "process.txt", sep="/"), row.names=F, col.names=F)