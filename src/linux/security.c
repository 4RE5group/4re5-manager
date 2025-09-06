#include "defs.h"
#include "config.h"

#define FORBIDDEN_CHARACTERS "/~$:;!?%*^`"
#define REPLACEMENT_CHARACTER '-'

/*
	Sanitize input buffer to avoid path injection.
*/
void	sanitizePath(char *path)
{
	size_t	i = 0;
	size_t	j = 0;
	while (path[i])
	{
		j = 0;
		while(FORBIDDEN_CHARACTERS[j])
		{
			if (path[i] == FORBIDDEN_CHARACTERS[j])
				path[i] = REPLACEMENT_CHARACTER;
			j++;
		}
		// remove '..' -> '-.' no parent directory access
		if (path[i + 1] && path[i] == path[i + 1] && path[i] == '.')
			path[i] = REPLACEMENT_CHARACTER;
		i++;
	}
}
