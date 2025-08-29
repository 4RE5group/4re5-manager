#include "defs.h"
#include "config.h"

const char	*json_getkey(json_t *value, char *key)
{
	return (json_string_value(json_object_get(value, key)));
}

int	is_installed(const char *package)
{
	char		APP_PATH_PARSED[512];
	const char	*home_dir;
	char		package_path[512];

	home_dir = getenv("HOME");
	if (!home_dir)
	{
		putstrf("%s[%sx%s] Error, please set HOME environement variable%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		return (0);
	}
	strformat(APP_DIR, APP_PATH_PARSED, home_dir);
	strformat(PACKAGES_DIR, package_path, APP_PATH_PARSED, package);

	// check if ~/4re5 group/packages/package/ exists
	return (access(package_path, F_OK) != -1);
}

json_t	*get_repo()
{
	char	REPO_PATH_PARSED[512];
	const char	*home_dir;
	json_t *root;
	json_error_t error;

	home_dir = (const char *)0;
	// parse file paths
	home_dir = getenv("HOME");
	if (!home_dir)
	{
		putstrf("%s[%sx%s] Error, please set HOME environement variable%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
		return ((json_t *)0);
	}
	strformat(REPO_PATH, REPO_PATH_PARSED, home_dir);

	// Load JSON file
	root = json_load_file(REPO_PATH_PARSED, 0, &error);
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

int	load_json_repo(short only_names, const char *search_query)
{
	json_t *root;
	size_t index;
	json_t *value;
	int	total_elements;
	
	total_elements = -1;

	root = get_repo();
	if (!root)
		return (total_elements);
		
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
				const char	*version = "not found";
				json_t	*platform;
				json_array_foreach(json_object_get(value, "platform"), platformid, platform) 
				{
					// if linux, display it
					if (json_integer_value(json_object_get(platform, "platformid")) == PLATFORM_LINUX)
					{
						version = json_getkey(platform, "version");
						break;
					}
				}
				if (!platform || strcmp(version, "not found") == 0)
					continue;
				char	name_line[512];
				total_elements++;
				strformat("%s%s%s@%sv%s    ", name_line, COLOR_YELLOW, json_getkey(value, "package"), COLOR_BGREEN, COLOR_WHITE, version);
				// fill with spaces until 60th character
				int	name_line_len = strlen(name_line) % 60;
				while (name_line_len < 60)
					name_line[name_line_len++] = ' ';
				name_line[name_line_len] = '\0';

				// display if is installed
				if (is_installed(json_getkey(value, "package")))
					putstrf("%s ⤷ installed (as %s '%s')", 1, name_line, (strcmp(json_getkey(platform, "isCmdTool"), "true") == 0)?"the cli util":"the gui app", json_getkey(platform, "startUpFile"));
				else
					putstrf("%s ⤷ not installed", 1, name_line);

				write(1, "\n", 1);
				if (only_names)
					continue;
				putstrf("    %sname%s:          %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "name"), COLOR_RESET);	
				putstrf("    %sdescription%s:   %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "description"), COLOR_RESET);
				putstrf("    %sauthor%s:        @%s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "author"), COLOR_RESET);
				putstrf("    %slicense%s:       %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "license"), COLOR_RESET);
				putstrf("    %sfirst release%s: %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "first_date"), COLOR_RESET);
				putstrf("    %sdocs%s:          %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "docs"), COLOR_RESET);
				putstrf("    %sgithub url%s:    %s%s\n", 1, COLOR_BGREEN, COLOR_WHITE, json_getkey(value, "github"), COLOR_RESET);


				putstrf("\n", 1);
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
		if (fetch_url(REPO_URL, REPO_PATH_PARSED) == 1)
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
		putstrf("4re5 manager, version %s:%s\nUsage:  %s [option]\n        %s [option] package_name\n\n", 1, VERSION, BUILD_TYPE, argv[0], argv[0]);
		putstrf("Official 4re5 app downloader & updater with 4re5 security features for authentification and more\n\n", 1);
		putstrf("4re5 manager options:\n\
    list [full]\n\
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
	else if (strcmp(argv[1], "version") == 0)
		putstrf("4re5 manager %s\nVersion build-type: %s\nVersion build date: %s\nRun '%s --help' for more info\n", 1, \
				VERSION, BUILD_TYPE, BUILD_DATE, argv[0]);
	else if (strcmp(argv[1], "list") == 0)
	{
		int total_elem = -1;
		if (argc == 2) // 4re5-manager list command
			total_elem = load_json_repo(1, 0); 
		else if (argc == 3 && strcmp(argv[2], "full") == 0) // full details
			total_elem = load_json_repo(0, 0);
		if(total_elem != -1)
			putstrf("%sSuccessfully displayed %i elements%s\n", 1, COLOR_YELLOW, total_elem, COLOR_RESET);
		else
			putstrf("%s[%sx%s] An error occurred while loading apps%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
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
	else if (strcmp(argv[1], "install") == 0)
	{
		if (argc != 3)
		{
			putstrf("%s[%sx%s] Error, format: %s install [package name]%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, argv[0], COLOR_RESET);
			return (1);
		}

		char		APP_PATH_PARSED[512];
		const char	*home_dir;
		char		package_path[512];
	
		home_dir = getenv("HOME");
		if (!home_dir)
		{
			putstrf("%s[%sx%s] Error, please set HOME environement variable%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
			return (0);
		}
		strformat(APP_DIR, APP_PATH_PARSED, home_dir);
		strformat(PACKAGES_DIR, package_path, APP_PATH_PARSED, "");

		// check if ~/4re5 group/packages/ exists
		if (access(package_path, F_OK) == -1)
			if (!mkdir(package_path, 0777))
				putstrf("Successfully initialised packages directory\n", 1);
			
		// get app download url & data
		json_t	*root;
		json_t	*value;
		size_t	i;

		root = get_repo();
		if (!root)
			return (1);

		const char	*package;
		short		found = 0;
		char		installation_output[512];
		json_array_foreach(root, i, value)
		{
			package = json_getkey(value, "package");
			if (strcmp(package, argv[2]) == 0)
			{
				size_t	j;
				json_t	*platform;
				json_array_foreach(json_object_get(value, "platform"), j, platform)
				{
					if (json_integer_value(json_object_get(platform, "platformid")) == PLATFORM_LINUX)
					{
						strformat(PACKAGES_DIR, installation_output, APP_PATH_PARSED, package);
						if (access(installation_output, F_OK) == -1)
							if (!mkdir(installation_output, 0777))
								putstrf("%s[%s+%s] Successfully initialised package directory!\n", 1, COLOR_BGREEN, COLOR_GREEN, COLOR_BGREEN, COLOR_RESET);
						putstrf("Found package: %s%s%s, version: %sv%s\n%s[%s~%s] Starting installation into %s/...%s\n", 1, COLOR_BGREEN, package, COLOR_RESET, COLOR_BGREEN, json_getkey(platform, "version"), COLOR_BYELLOW, COLOR_YELLOW, COLOR_BYELLOW, installation_output, COLOR_RESET);
						
						putstrf("%s[%s~%s] downloading %s...%s\n", 1, COLOR_BYELLOW, COLOR_YELLOW, COLOR_BYELLOW, json_getkey(platform, "url"), COLOR_RESET);

						char	output_file[512];
						strformat("%s/%s", output_file, installation_output, basename((char *)json_getkey(platform, "url")));
						fetch_url((char *)json_getkey(platform, "url"), output_file);

						found = 1;
						break;
					}
				}
			}
		}
		if (found)
			putstrf("Successfully installed %s on your system!\n", 1, argv[2]);
		else
			putstrf("%s[%sx%s] Cound not find requested package, try maybe updating using '%s update'%s\n", 2, COLOR_BRED, COLOR_RED, COLOR_BRED, argv[0], COLOR_RESET);

	}
	else
		putstrf("%s[%sx%s] Invalid argument at position id:1%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, COLOR_RESET);
	return (0);
}
