#include "defs.h"

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

int	strformat(char *str, char *buf, ...)
{
	va_list	args;
	int	res;

	va_start(args, buf);

	res = strformat_va(str, buf, args);
	buf[res] = '\0';
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
