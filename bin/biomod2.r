setwd("@Target")
library("biomod2")
# species occurrences
DataSpecies <- read.csv("@Filename")
head(DataSpecies)
# the name of studied species
myRespName <- '@SpeciesName'
# the presence/absences data for our species
myResp <- as.numeric(DataSpecies[,myRespName])
# the XY coordinates of species data
myRespXY <- DataSpecies[,c("X_WGS84","Y_WGS84")]
# Environmental variables
myExpl = stack("1.tif", "2.tif", "3.tif")
# 1. Formatting Data
myBiomodData <- BIOMOD_FormatingData(resp.var = myResp,
                                     expl.var = myExpl,
                                     resp.xy = myRespXY,
                                     resp.name = myRespName,
                                     PA.nb.rep = 1, 
                                     PA.nb.absences = 1100,
                                     PA.strategy = 'random',
                                     na.rm = FALSE)
# 2. Defining Models Options using default options.
myBiomodOptions <- BIOMOD_ModelingOptions()
# 3. Doing Modelisation
myBiomodModelOut <- BIOMOD_Modeling( myBiomodData,
                                     models = c('GLM'),
                                     models.options = myBiomodOptions,
                                     NbRunEval=1,
                                     DataSplit=80,
                                     VarImport=0,
                                     models.eval.meth = c('ROC'),
                                     do.full.models=FALSE,
                                     modeling.id="test")
## print a summary of modeling stuff
myBiomodModelOut
##Projection on current environemental conditions
myBiomodProjection <- BIOMOD_Projection(modeling.output = myBiomodModelOut,
                                        new.env = myExpl,
                                        proj.name = 'current',
                                        selected.models = 'all',
                                        binary.meth = 'TSS',
                                        compress = FALSE,
                                        build.clamping.mask = FALSE,
                                        do.stack=FALSE,
                                        output.format=".img")


