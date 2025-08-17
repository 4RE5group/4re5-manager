#include <unistd.h>
#include <stdarg.h>
#include <string.h>


#define COLOR_BLACK	"\e[30m"
#define COLOR_RED	"\e[31m"
#define COLOR_GREEN	"\e[32m"
#define COLOR_YELLOW	"\e[33m"
#define COLOR_BLUE	"\e[34m"
#define COLOR_MAGENTA	"\e[35m"
#define COLOR_CYAN	"\e[36m"
#define COLOR_WHITE	"\e[37m"

#define COLOR_BBLACK	"\e[90m"
#define COLOR_BRED	"\e[91m"
#define COLOR_BGREEN	"\e[92m"
#define COLOR_BYELLOW	"\e[93m"
#define COLOR_BBLUE	"\e[94m"
#define COLOR_BMAGENTA	"\e[95m"
#define COLOR_BCYAN	"\e[96m"
#define COLOR_BWHITE	"\e[97m"

#define COLOR_RESET "\e[0m"

int	count_occurences(char *str, char *charset)
{
	size_t	i;
	int	count;
	short	j;

	i = 0;
	count = 0;
	while (i < strlen(str))
	{
		j = 0;
		while (str[i + j] && charset[j] && str[i + j] == charset[j])
			j++;
		if (charset[j] == '\0')
			count++;
		i+=j + 1;
	}
	return (count);
}

void	putstrf(char *str, int fd, ...)
{
	size_t	i;
	int	buf_pos;
	va_list	args;
	int	count;
	int	arg_pos;
	char	buf[4096];
	char	*current;

	count = count_occurences(str, "%s");
	arg_pos = 0;
	va_start(args, fd); // init args to be after var fd
	i = 0;
	buf_pos = 0;
	while (str[i])
	{
		if (arg_pos < count && strncmp(&str[i], "%s", 2) == 0)
		{
			current = va_arg(args, char *);
			while (*current)
				buf[buf_pos++] = *current++;
			arg_pos++;
			i++;
		}
		else
			buf[buf_pos++] = str[i];
		i++;
	}
	write(fd, buf, buf_pos);
	va_end(args); // clean up
}

int	main(int argc, char **argv)
{
	(void)argv;
	if (argc == 1)
	{
		putstrf("%s[%sx%s] Invalid arguments, use --help to get list of valid arguments%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		return (1);
	}
	return (0);
}
