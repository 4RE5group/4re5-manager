MSBUILD			= C:\Windows\Microsoft.NET\Framework64\v4.0.30319\MSBuild.exe
OUTFILE			= 4re5-manager.exe
CS_FILES		= 4re5-manager.cs
MSBUILD_FLAGS	= /t:Compile /p:Configuration=Release
SIGNTOOL		= "C:\Program Files (x86)\Windows Kits\10\bin\10.0.22621.0\x64\signtool.exe"
# CSC_FLAGS		= /out:$(OUTFILE) /win32res:app.res /target:winexe /win32manifest:app.manifest /platform:anycpu /win32icon:icon.ico /optimize+ /warnaserror

# all:		$(CS_FILES)
# 	$(CSC) $(CSC_FLAGS) $(CS_FILES)

all:		$(CS_FILES)
	$(MSBUILD) $(MSBUILD_FLAGS)

run:		all
	call $(OUTFILE)

.PHONY: all run