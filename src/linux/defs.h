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

typedef struct platform {
	char	*name;
	int	platformid;
	char	*url;
	char	*version;
	char	*install_requires[32];
	short	isCmdTool;
	char	*startUpFile;
	short	isZipped;
	short	isInstaller;
} platform;

typedef	struct APP_STRUCT {
	const char		*version;
	const char		*name;
	const char		*description;
	const char		*image;
	const char		*first_date;
	const char		*author;
	const char		*price;
	const char		*docs;
	const char		*github;
	const char		*license;
	const char		*package;
	short			platforms[3];
	const char	*keywords[32];
} APP_STRUCT;


// utils.c
void	putstrf(char *str, int fd, ...);
int	strformat_va(char *str, char *buf, va_list args);
int	strformat(char *str, char *buf, ...);

// fetch_repo.c
int	fetch_repo(char *url, char *output_file);

#endif
