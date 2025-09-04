#include "defs.h"
#include "config.h"

/*
	A simple printf.
*/
void	putstrf(char *str, int fd, ...)
{	
	char	buf[4096];
	int	buf_pos;
	va_list args;
		va_start(args, fd);

	buf_pos = strformat_va(str, buf, args);
	write(fd, buf, buf_pos);
	va_end(args);
}

/*
	Needed to be able to extract strings from repo without writing big lines
*/
const char	*json_getkey(json_t *value, char *key)
{
	return (json_string_value(json_object_get(value, key)));
}

/*
	Reads the json file associated to the repository.
*/
json_t	*get_repo()
{
	json_t *root;
	json_error_t error;

	// Load JSON file
	root = json_load_file(REPO_PATH, 0, &error);
	if (!root) {
		putstrf("%s[%sx%s] An error occured while loading JSON repository \n\t=> '%s'%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, error.text, COLOR_RESET);
		return ((json_t *)0);
	}
	// Check if root is an array
	if (!json_is_array(root)) {
		putstrf("%s[%sx%s] JSON repository is invalid, try updating it or try again later%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		json_decref(root);
		return ((json_t *)0);
	}

	return (root);
}

/*
	Converts a string formatted as 4re5 versions (v1.xx.xxx) to a float.
	ex:
		v1.123.456 => 1.123456
	needed to compare versions
*/
float	version_to_number(char *version)
{
	float	num = 0.0f;
	size_t	i = 0;
	float	size = 0.1f;

	// int part
	while(version && version[i] && version[i] != '.')
	{
		// if number
		if (version[i] >= '0' && version[i] <= '9')
			num = num * 10 + (version[i] - '0');
		i++;
	}
	// decimals part
	while(version && version[i])
	{
		// if number
		if (version[i] >= '0' && version[i] <= '9')
		{
			num += size * (version[i] - '0');
			size /= 10;
		}
		i++;
	}
	return (num);
}

/*
	Display an error and exit with return code of 1.
*/
void	puterror(char	*msg)
{
	putstrf("%s[%sx%s] %s%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, msg, COLOR_RESET);
	exit(1);
}

/*
	Count the occurence of a charset inside a string.
*/
int	count_occurrences(char *str, char *charset)
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
		{
			count++;
			i += j;
		}
		else
			i++;
	}
	return (count);
}

int	strformat(char *input, char *output, ...)
{
	va_list	args;
	int	res;

	va_start(args, output);

	res = strformat_va(input, output, args);
	output[res] = '\0';
	va_end(args);
	return (res);
}

int strformat_va(char *str, char *buf, va_list args) {
	size_t i = 0;
	int buf_pos = 0;
	int arg_pos = 0;
	int count = count_occurrences(str, "%s") + count_occurrences(str, "%i");

	while (str[i])
	{
		if (arg_pos <= count && str[i] == '%' && (str[i + 1] == 's' || str[i + 1] == 'i')) 
		{
			if (strncmp(&str[i], "%s", 2) == 0) 
			{
				char *current = va_arg(args, char *);
				while (*current)
					buf[buf_pos++] = *current++;
				i += 2;
			} 
			else if (strncmp(&str[i], "%i", 2) == 0) 
			{
				int tmp = va_arg(args, int);
				char num_buf[12]; // Enough for 32-bit int
				int num_pos = 0;

				// Handle negative numbers
				if (tmp < 0) {
					buf[buf_pos++] = '-';
					tmp = -tmp;
				}

				// Handle zero
				if (tmp == 0) {
					buf[buf_pos++] = '0';
					i += 2;
					arg_pos++;
					continue;
				}

				// simple itoa 
				while (tmp > 0) {
					num_buf[num_pos++] = '0' + (tmp % 10);
					tmp /= 10;
				}

				// Write digits in correct order
				for (int j = num_pos - 1; j >= 0; --j) {
					buf[buf_pos++] = num_buf[j];
				}
				i += 2;
			}
			arg_pos++;
		} 
		else
			buf[buf_pos++] = str[i++];
	}
	buf[buf_pos] = '\0'; // Null-terminate the buffer
	return buf_pos;
}

short	contains(const char *from, const char *charset)
{
	int	charset_size = strlen(charset);
	int	input_size = strlen(from);
	int	i;

	i = 0;
	while (from[i] && i <= input_size - charset_size)
	{
		if (strncmp(&from[i], charset, charset_size) == 0)
			return 1;
		i++;
	}
	return 0;
}
