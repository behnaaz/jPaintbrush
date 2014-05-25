@set PATH=D:/System/bin;%PATH%
@set version=1.0
@set package_name=paintbrush-%version%
@set dependencies=dependencies\about-2.0.jar

rem Cleaning
@rm -rf classes
@rm -rf releases\%package_name%

rem Compiling source
@md classes
@javac -Xlint -encoding utf8 -g:none -classpath %dependencies% -d classes src/simoes/programs/paintbrush/*.java

rem Creating jar library
@md releases\%package_name%
@cp dependencies\* releases\%package_name%
@jar -cmf paint.mf releases\%package_name%\%package_name%.jar -C classes .
@jarsigner -keystore ../mykey -storepass 12345qwerty_12345 -keypass 12345qwerty_12345 releases\%package_name%\%package_name%.jar brunogsimoes

@pause
