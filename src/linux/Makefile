OUT				= 4re5-manager
C				= gcc
CFLAGS			= -Wall -Wextra -Werror
LDFLAGS			= -lcurl -lssl -lcrypto -ljansson
C_FILES			= main.c utils.c fetch_repo.c installation.c security.c
O_FILES			= $(C_FILES:.c=.o)
DEB_FOLDER		= release-deb
DEB_FILE_NAME	= release.deb
DEPENDENCIES	= libcurl4-openssl-dev libjansson-dev libssl-dev

# Cross-compilation toolchains
CC_x86_64		= gcc -m64

# Output binary names for each architecture
OUT_x86_64		= $(OUT)_x86_64

all: $(OUT_x86_64)

$(OUT_x86_64): $(O_FILES)
	$(CC_x86_64) $(CFLAGS) $^ -o $@ $(LDFLAGS)

%.o: %.c
	$(C) $(CFLAGS) -c $< -o $@

release: all
	$(RM) -rf /tmp/$(DEB_FOLDER)
	cp $(OUT_x86_64) release-deb/opt/com.ares/
	cp -r $(DEB_FOLDER) /tmp/$(DEB_FOLDER)
	chmod 755 /tmp/$(DEB_FOLDER)/DEBIAN
	chmod 644 /tmp/$(DEB_FOLDER)/DEBIAN/control
	dpkg-deb --build /tmp/$(DEB_FOLDER)
	cp /tmp/$(DEB_FOLDER).deb ./$(DEB_FILE_NAME)

clean:
	$(RM) -rf $(O_FILES)

fclean: clean
	$(RM) -rf $(OUT_x86_64) $(DEB_FILE_NAME)

re: fclean all

deps:
	sudo apt install $(DEPENDENCIES) -y

.PHONY: all release clean fclean re deps
