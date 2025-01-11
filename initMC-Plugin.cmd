@echo off

REM /*
REM  * Build Minecraft-Plugin folders.
REM  * 20241130 Jonas@Saslona.com
REM  */

REM -- Write .gitignore ---
echo= > .gitignore
echo # Ignore Gradle project-specific cache directory >> .gitignore
echo .gradle/ >> .gitignore
echo= >> .gitignore
echo # Ignore Gradle build output directory >> .gitignore
echo build/ >> .gitignore
echo= >> .gitignore
echo # VSCode >> .gitignore
echo .vscode/ >> .gitignore
echo= >> .gitignore
echo # Minecraft-Plugin >> .gitignore
echo bin/ >> .gitignore
echo= >> .gitignore
echo # User-specific stuff >> .gitignore
echo .idea/ >> .gitignore
echo= >> .gitignore
echo # Compiled class file >> .gitignore
echo *.class >> .gitignore
echo= >> .gitignore
echo # Package Files >> .gitignore
echo *.jar >> .gitignore

REM --- Write build.gradle ---
curl "https://share.saslona.com/build.gradle" -o build.gradle

REM --- Delete unused Folders ---
del /f /s /q app 1>nul
rmdir /s /q app

REM -- Build new Folder structure ---
md src
cd src/
md main
cd main/
md resources
md java
cd java/
echo "give folder structure example: me.gaminglounge.project"
set /p "directory=folder structure: "
set packages=%directory:.=\%
md "%packages%"
set "projectname=Main"
set /p "projectname=Project name[Main]: "

REM --- Create Paper-Plugin.yml ---
cd ../resources
echo name: %projectname%> paper-plugin.yml
echo version: '${version}' >> paper-plugin.yml
echo main: %directory%.%projectname%>> paper-plugin.yml
echo description: >> paper-plugin.yml
echo api-version: '1.21' >> paper-plugin.yml
echo authors: >> paper-plugin.yml
echo   - "NAME" >> paper-plugin.yml

REM --- Create main.java ---
cd ../java/%packages%/
echo package %directory%; > %projectname%.java
echo= >> %projectname%.java
echo import java.util.logging.Level; >> %projectname%.java
echo= >> %projectname%.java
echo import org.bukkit.plugin.PluginManager; >> %projectname%.java
echo import org.bukkit.plugin.java.JavaPlugin; >> %projectname%.java
echo= >> %projectname%.java
echo public final class %projectname% extends JavaPlugin { >> %projectname%.java
echo= >> %projectname%.java
echo     public static %projectname% INSTANCE; >> %projectname%.java
echo= >> %projectname%.java
curl "https://share.saslona.com/MC-Plugin_init_curl_part" >> %projectname%.java
echo= >> %projectname%.java
echo } >> %projectname%.java