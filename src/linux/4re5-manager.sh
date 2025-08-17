#/usr/bin/bash

COLUMNS=$(tput cols)
LINES=$(tput lines)

WINDOW_WIDTH=60
WINDOW_HEIGHT=20
WINDOW_NAME="4re5 manager"

print_line() {
	# is_first_line starting_char middle_char ending_char
	for p in $(seq 1 $[$[$COLUMNS / 2] - $[$WINDOW_WIDTH / 2]]); do
		echo -n " "
	done
	echo -n "$2"

	local max=$[$WINDOW_WIDTH - 2]
	local x=0
	while [ $x -lt $max ]; do
		if [[ $1 == true && $x -eq 2 ]]; then
			echo -n "$WINDOW_NAME"
			((x+=$(echo "$WINDOW_NAME" | wc -m)-1))
		fi
		echo -n "$3"
		((x++))
	done
	echo "$4"
}




main() {
	clear
	current_line=0;
	for p in $(seq 1 $[$LINES / 2 - $WINDOW_HEIGHT / 2]); do
		echo ""
		((current_line++))
	done
	print_line true "┌" "─" "┐"
	for y in $(seq 1 $[$WINDOW_HEIGHT - 2]); do
		print_line false "│" " " "│"
		((current_line++))
	done
	print_line false "└" "─" "┘"
	
	while [ $current_line -lt $LINES ]; do
		echo ""
		((current_line++))
	done
}


main
