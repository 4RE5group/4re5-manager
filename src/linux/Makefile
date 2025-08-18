OUT = 4re5-manager
C = gcc
CFLAGS = -Wall -Wextra -Werror -g3
LDFLAGS = -lcurl -lssl -lcrypto -ljansson
C_FILES = main.c utils.c fetch_repo.c
O_FILES = $(C_FILES:.c=.o)

all: $(OUT)

$(OUT): $(O_FILES)
	$(C) $(CFLAGS) $^ -o $@ $(LDFLAGS)

%.o: %.c
	$(C) $(CFLAGS) -c $< -o $@

clean:
	rm -rf $(OUT) $(O_FILES)

.PHONY: all clean
