@GOTO start

:add
 @set X=%X%;%1
 @GOTO :eof

:start
@set X=.\dist\ProM-Framework.jar
@set X=%X%;.\dist\ProM-Contexts.jar
@set X=%X%;.\dist\ProM-Models.jar
@set X=%X%;.\dist\ProM-Plugins.jar

@for /R .\lib %%I IN ("*.jar") DO @call :add .\lib\%%~nI.jar

@set IMPORTLOG=%1
@set EXPORTMODEL=%2.pnml
@set OUTPUT=%2.discover.log

java -da -Xmx8G -classpath "%X%" -Djava.library.path=.//lib -Djava.util.Arrays.useLegacyMergeSort=true org.processmining.contexts.cli.CLI -f Scripts/Discover.txt 1> %OUTPUT% 2>&1

set X=
