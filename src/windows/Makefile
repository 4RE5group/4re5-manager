NAME			= 4re5-manager.exe
DOTNET	  		= dotnet
DOTNET_BUILD	= publish
BUILD	   		= Release
PLATFORM		= win-x64
DOTNETFLAGS		= --self-contained true /p:PublishSingleFile=true

SRCS			= 4re5-manager.cs \
			 	 API.cs \
			 	 Json.cs \
			 	 Security.cs \
			 	 app\main.html \
			 	 app\splash.html

signtool		= "C:\Program Files (x86)\Windows Kits\10\bin\10.0.22621.0\x64\signtool.exe"

all: $(NAME)

$(NAME): $(SRCS)
	$(DOTNET) $(DOTNET_BUILD) -c $(BUILD) -r $(PLATFORM) $(DOTNETFLAGS)
	xcopy /I /C /F /Y ".\bin\$(BUILD)\net8.0-windows\$(PLATFORM)\publish\$(NAME)" "$(NAME)"

clean:
	if exist "$(NAME).WebView2" rmdir /S /Q "$(NAME).WebView2"
	if exist "obj" rmdir /S /Q "obj"
	if exist "bin" rmdir /S /Q "bin"

fclean: clean
	if exist "$(NAME)" del /Q /F "$(NAME)"

re: fclean all

.PHONY: all clean fclean re
