#ifndef DEFS_H
#define DEFS_H

#include <unistd.h>
#include <string.h>
#include <errno.h>
#include <sys/stat.h>
#include <sys/types.h>
#include <stdarg.h>
#include <curl/curl.h>
#include <stdlib.h>
#include <libgen.h>

#include <jansson.h>

// definitions
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

#define COLOR_RESET	"\e[0m"

// platforms id
#define PLATFORM_WINDOWS 0
#define PLATFORM_ANDROID 1
#define PLATFORM_LINUX   2

// utils.c
void		putstrf(char *str, int fd, ...);
int		strformat_va(char *str, char *buf, va_list args);
int		strformat(char *str, char *buf, ...);
short		contains(const char *from, const char *charset);
const char	*json_getkey(json_t *value, char *key);
float		version_to_number(char *version);
json_t		*get_repo();
void	puterror(char	*msg);

// fetch_repo.c
int		fetch_url(char *url, char *output_file);

// installation.c
int		install(char *package_name);

#endif
