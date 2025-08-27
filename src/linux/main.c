#include "defs.h"

// repo informations
#define APP_DIR		"%s/.4re5 group/"
#define REPO_PATH	"%s/.4re5 group/repo.json"
#define REPO_URL	"https://raw.githubusercontent.com/4RE5group/4re5-repository/refs/heads/main/repo.json"

const char	*json_getkey(json_t *value, char *key)
{
	return (json_string_value(json_object_get(value, key)));
}

int	load_json_repo(short only_names, const char *search_query)
{
	char	REPO_PATH_PARSED[512];
	const char	*home_dir;
	json_t *root;
	json_error_t error;
	size_t index;
	json_t *value;
	int	total_elements;
	
	total_elements = -1;

	home_dir = (const char *)0;
	// parse file paths
	home_dir = getenv("HOME");
	strformat(REPO_PATH, REPO_PATH_PARSED, home_dir);

	// Load JSON file
	root = json_load_file(REPO_PATH_PARSED, 0, &error);
	if (!root) {
		putstrf("%s[%sx%s] An error occured while loading JSON repository \n\t=> '%s'%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, error.text, COLOR_RESET);
		return (total_elements);
	}
	// Check if root is an array
	if (!json_is_array(root)) {
		putstrf("%s[%sx%s] JSON repository is invalid, try updating it or try again later%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		json_decref(root);
		return (total_elements);
	}	
	// Iterate through the array
	json_array_foreach(root, index, value) 
	{
		if (json_is_object(value)) 
		{
			const char	*disabled = json_getkey(value, "disabled");
			if (disabled && strcmp(disabled, "true") == 0)
				continue;
			
			// apply search query 
			if (search_query == 0 
					|| contains(json_getkey(value, "package"), search_query)
					|| contains(json_getkey(value, "name"), search_query)
					|| contains(json_getkey(value, "description"), search_query))
			{

				// extract version
				int	platformid = 0;
				json_t	platform;
				if (json_array_foreach(json_array_value(json_object_get(value, "platform")), platformid, platform)) {
					
				}
				total_elements++;
				putstrf("%s%s%s@%s%s\n", 1, COLOR_YELLOW, json_getkey(value, "package"), COLOR_BYELLOW, COLOR_YELLOW, "soon");
				if (only_names)
					continue;
				putstrf("    %sname: %s%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(value, "name"), COLOR_RESET);
				putstrf("    %sdescription: %s%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(value, "description"), COLOR_RESET);
				putstrf("    %sauthor: \e[48;2;255;133;0m\e[38;2;255;255;255m@%s%s\n", 1, COLOR_YELLOW, json_getkey(value, "author"), COLOR_RESET);
				putstrf("    %sfirst release: %s%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(value, "first_date"), COLOR_RESET);
				putstrf("    %sname: %s%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(value, "name"), COLOR_RESET);
			}
		}
	}
	json_decref(root);
	return (total_elements);
}


void	check_repo(short force_updating)
{
	int	status;
	char	APP_DIR_PARSED[512];
	char	REPO_PATH_PARSED[512];
	const char	*home_dir;

	home_dir = (const char *)0;
	// parse file paths
	home_dir = getenv("HOME");
	strformat(APP_DIR, APP_DIR_PARSED, home_dir);
	strformat(REPO_PATH, REPO_PATH_PARSED, home_dir);

	// check if repo json file exists
	if (access(REPO_PATH_PARSED, F_OK) == -1)
		force_updating = 1;

	if (force_updating == 1)
	{
		status = mkdir(APP_DIR_PARSED, 0777);
		if (status != 0 && errno != EEXIST)
		{
			putstrf("%s[%sx%s] Could not fetch the repository%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
			putstrf("Folder: '%s'\n%s\n", 2, APP_DIR_PARSED, strerror(errno));
			return ;
		}
		// fetch the repository json file
		if (fetch_repo(REPO_URL, REPO_PATH_PARSED) == 1)
		{
			putstrf("%s[%sx%s] Could not download the file%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
			return ;
		}
		putstrf("%s[%s+%s] Updated the repository successfully!%s\n", 1, COLOR_YELLOW, COLOR_BYELLOW, COLOR_YELLOW, COLOR_RESET);
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
		check_repo(1); // force updating repo
	else if (strcmp(argv[1], "list") == 0)
	{
		int total_elem = load_json_repo(1, 0); 
		if(total_elem != -1)
			putstrf("%sSuccessfully displayed %i elements%s\n", 1, COLOR_YELLOW, total_elem, COLOR_RESET);
		else
			putstrf("%s[%sx%s] Error while loading app list%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
	}
	else if (strcmp(argv[1], "search") == 0)
	{
		if (argc >= 3)
		{
			int total_elem = load_json_repo(0, argv[2]);
			if (total_elem != -1)
				putstrf("%sSuccessfully displayed %i elements from query%s\n", 1, COLOR_YELLOW, total_elem, COLOR_RESET);
			else
				putstrf("%s[%sx%s] No result to be shown here%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);

		}
		else 
			putstrf("%s[%sx%s] Please enter a search query%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
	}
	else
		putstrf("%s[%sx%s] Invalid argument at position id:1%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
	return (0);
}
