cd /Users/huijieqiao/Programs/Java/NicheA_support/mac/release/App

mkdir NicheA.app/Contents/Frameworks
cp -R /usr/local/lib/libpcre.1.dylib NicheA.app/Contents/Frameworks/

##libgdaljni.dylib
install_name_tool -id @loader_path/libgdaljni.dylib NicheA.app/Contents/Java/libgdaljni.dylib 
install_name_tool -change /usr/local/lib/libpcre.1.dylib @loader_path/../Frameworks/libpcre.1.dylib NicheA.app/Contents/Java/libgdaljni.dylib 
otool -L NicheA.app/Contents/Java/libgdaljni.dylib 

##libgdalconstjni.dylib
install_name_tool -id @loader_path/libgdalconstjni.dylib NicheA.app/Contents/Java/libgdalconstjni.dylib 
install_name_tool -change /usr/local/lib/libpcre.1.dylib @loader_path/../Frameworks/libpcre.1.dylib NicheA.app/Contents/Java/libgdalconstjni.dylib 
otool -L NicheA.app/Contents/Java/libgdalconstjni.dylib 

##libogrjni.dylib
install_name_tool -id @loader_path/libogrjni.dylib NicheA.app/Contents/Java/libogrjni.dylib 
install_name_tool -change /usr/local/lib/libpcre.1.dylib @loader_path/../Frameworks/libpcre.1.dylib NicheA.app/Contents/Java/libogrjni.dylib 
otool -L NicheA.app/Contents/Java/libogrjni.dylib 

##libosrjni.dylib
install_name_tool -id @loader_path/libosrjni.dylib NicheA.app/Contents/Java/libosrjni.dylib 
install_name_tool -change /usr/local/lib/libpcre.1.dylib @loader_path/../Frameworks/libpcre.1.dylib NicheA.app/Contents/Java/libosrjni.dylib 
otool -L NicheA.app/Contents/Java/libosrjni.dylib 




##libpcre.1.dylib
install_name_tool -id @loader_path/../Frameworks/libpcre.1.dylib NicheA.app/Contents/Frameworks/libpcre.1.dylib
otool -L NicheA.app/Contents/Frameworks/libpcre.1.dylib
