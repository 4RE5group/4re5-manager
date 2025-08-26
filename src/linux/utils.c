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
		i += j + 1;
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

int	strformat_va(char *str, char *buf, va_list args)
{
	size_t	i;
	int	buf_pos;
	int	count;
	int	arg_pos;
	char	*current;

	count = count_occurences(str, "%s") + 1;
	arg_pos = 0;
	i = 0;
	buf_pos = 0;
	while (str[i])
	{
		if (arg_pos < count)
		{
			if (strncmp(&str[i], "%s", 2) == 0)
			{
				current = va_arg(args, char *);
				while (*current)
					buf[buf_pos++] = *current++;

			}
			else if (strncmp(&str[i], "%i", 2) == 0)
			{
				int	tmp = va_arg(args, int);
				int	tmp2 = 0;
				int	size = 0;
				
				// simple itoa
				if (tmp == 0)
				{
					buf[buf_pos++] = '0';
					continue;
				} 
				else if (tmp == -2147483647)
				{
					buf[buf_pos++] = '-';
					buf[buf_pos++] = '2';
					buf[buf_pos++] = '1';
					buf[buf_pos++] = '4';
					buf[buf_pos++] = '7';
					buf[buf_pos++] = '4';
					buf[buf_pos++] = '8';
					buf[buf_pos++] = '3';
					buf[buf_pos++] = '6';
					buf[buf_pos++] = '4';
					buf[buf_pos++] = '7';
					continue;
				}
				if (tmp < 0)
				{
					tmp = -tmp;
					buf[buf_pos++] = '-';
				}
				tmp2 = tmp;
				// calculate size
				while (tmp2 > 0)
				{
					buf_pos++;
					size++;
					tmp2 /= 10;
				}
				while(tmp > 0)
				{
					buf[buf_pos--] = '0' + (tmp % 10);
					tmp /= 10;
				}
				buf_pos+=size + 1;
			}
			else 
			{
				buf[buf_pos++] = str[i++];
				continue;
			}
			arg_pos++;
			i++;
		}
		else
			buf[buf_pos++] = str[i];
		i++;
	}
	return (buf_pos);
}
