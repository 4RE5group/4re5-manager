OUT=4re5-manager
C=cc
C_FLAGS=-Wall -Wextra -Werror
C_FILES=main.c
O_FILES=$(C_FILES:.c=.o)

all: $(O_FILES)
	$(C) $(C_FLAGS) $(C_FILES) -o $(OUT)

run: all
	clear && ./$(OUT)
	
clean:
	rm -rf $(OUT) $(O_FILES)

.PHONY:	all run clean
