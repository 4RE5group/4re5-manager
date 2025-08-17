#include "defs.h"

// repo informations
#define APP_DIR		"%s/.4re5 group/"
#define REPO_PATH	"%s/.4re5 group/repo.json"
#define REPO_URL	"https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json"

void	check_repo(short force_updating)
{
	int	status;
	char	APP_DIR_PARSED[512];
	char	REPO_PATH_PARSED[512];
	const char	*home_dir;

	home_dir = (const char *)0;
	// check if repo json file exists
	if (access(REPO_PATH, F_OK) == -1)
		force_updating = 1;

	if (force_updating)
	{
		// parse file paths
		home_dir = getenv("HOME");
		strformat(APP_DIR, APP_DIR_PARSED, home_dir);
		strformat(REPO_PATH, REPO_PATH_PARSED, home_dir);

		status = mkdir(APP_DIR_PARSED, 0777);
		if (status != 0 && errno != EEXIST)
		{
			putstrf("%s[%sx%s] Could not fetch the repository%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
			putstrf("Folder: '%s'\n%s\n", 2, APP_DIR_PARSED, strerror(errno));
			return ;
		}
		//  repository json file
		if (fetch_repo(REPO_URL, REPO_PATH_PARSED) == 1)
		{
			putstrf("%s[%sx%s] Could not download the file%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
			return ;
		}

		putstrf("%s[%s+%s] Updated the repository successfully!%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, COLOR_YELLOW, COLOR_RESET);
		putstrf("Repo path: %s\n", 1, REPO_PATH_PARSED);
	}
}

int	main(int argc, char **argv)
{
	if (argc == 1)
	{
		putstrf("%s[%sx%s] Invalid arguments, use --help to get list of valid arguments%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		return (1);
	}
	//  repo is it does not exist
	check_repo(0);
	if (strcmp(argv[1], "--help") == 0) {
		putstrf("4re5 manager, version v%s\nUsage:  %s [option]\n        %s [option] package_name\n\n", 1, "1.8", argv[0], argv[0]);
		putstrf("Official 4re5 app downloader & updater with 4re5 security features for authentification and more\n\n", 1);
		putstrf("4re5 manager options:\n\
    list\n\
    search <query>\n\
    install package_name\n\
    remove package_name\n\
    version\n\
    update\n\
    upgrade\n\
More information available at: https://github.com/4RE5group/4re5-manager\n", 1);
	}
	else if (strcmp(argv[1], "update") == 0)
	return (0);
}
