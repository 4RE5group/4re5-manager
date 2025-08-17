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

	count = count_occurences(str, "%s");
	arg_pos = 0;
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
	return (buf_pos);
}
