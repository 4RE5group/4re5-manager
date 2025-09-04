#include "defs.h"
#include "config.h"

/*
	Returns the version of installed app.
	If not installed returns -1
*/
float	get_package_version(const char *package)
{
	char		package_path[512];
	char		manifest_path[512];

	strformat(PACKAGES_DIR, package_path, APP_DIR, package);
	strformat("%s/manifest", manifest_path, package_path);

	// check if ~/4re5 group/packages/package/manifest exists
	if (access(manifest_path, F_OK) != -1)
	{
		json_t *root;
		json_error_t error;

		// Load JSON file
		root = json_load_file(manifest_path, 0, &error);
		if (!root) {
			putstrf("%s[%sx%s] An error occured while loading this manifest:\n\t=> '%s'", 2, COLOR_RED, COLOR_BRED, COLOR_RED, error.text);
			putstrf("\n\t=> package: %s", 2, package);
			putstrf("\n\t=> path:    %s\n", 2, manifest_path);
			return (-1.0);
		}
		return (version_to_number((char *)json_getkey(root, "version")));
	}
	return (-1.0);
}

/*
	Process json data.
*/
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
				float	current_version = get_package_version(json_getkey(value, "package"));
				if (current_version >= 0.0f)
				{
					// if can be updated
					float	lastest_version = version_to_number((char *)json_getkey(platform, "version"));
					if (lastest_version > current_version)
						putstrf("%s ⤷ an update is available", 1, name_line, json_getkey(platform, "version"));
					else
						putstrf("%s ⤷ installed (as %s '%s')", 1, name_line, (strcmp(json_getkey(platform, "isCmdTool"), "true") == 0)?"the cli util":"the gui app", json_getkey(platform, "startUpFile"));
				}
				else
					putstrf("%s ⤷ not installed", 1, name_line);

				write(1, "\n", 1);
				if (only_names)
					continue;
				putstrf("    %sname%s:          %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "name"));	
				putstrf("    %sdescription%s:   %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "description"));
				putstrf("    %sprice%s:         %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "price"));
				putstrf("    %sauthor%s:        @%s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "author"));
				putstrf("    %slicense%s:       %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "license"));
				putstrf("    %sfirst release%s: %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "first_date"));
				putstrf("    %sdocs%s:          %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "docs"));
				putstrf("    %sgithub url%s:    %s\n", 1, COLOR_BGREEN, COLOR_RESET, json_getkey(value, "github"));


				putstrf("\n", 1);
			}
		}
	}
	json_decref(root);
	return (total_elements);
}

/*
	Check if repository is installed, if not install it.
	Also has the possibility to override this with the force_updating flag.
*/
void	check_repo(short force_updating)
{
	int	status;

	// check if repo json file exists
	if (access(REPO_PATH, F_OK) == -1)
		force_updating = 1;

	if (force_updating == 1)
	{
		status = mkdir(APP_DIR, 0777);
		if (status != 0 && errno != EEXIST)
			puterror("Could not fetch the repository");
		// fetch the repository json file
		if (fetch_url(REPO_URL, REPO_PATH) == 1)
			puterror("Could not download the file");
		putstrf("%s[%s+%s] Updated the repository successfully!%s\n", 1, COLOR_BGREEN, COLOR_GREEN, COLOR_BGREEN, COLOR_RESET);
	}
}

int	main(int argc, char **argv)
{
	// check if user has administrative rights
	if (geteuid() != 0)
		puterror("Could not access 4re5 working directory. Are you root?");
	if (argc == 1)
		puterror("Invalid arguments, use --help to get list of valid arguments");
	//  repo is it does not exist
	check_repo(0);
	if (strcmp(argv[1], "--help") == 0) {
		putstrf("4re5 manager, version %s:%s\nUsage:  %s [option]\n        %s [option] package_name\n\n", 1, VERSION, BUILD_TYPE, argv[0], argv[0]);
		putstrf("Official 4re5 app downloader & updater with 4re5 security features for authentification and more\n\n", 1);
		putstrf("4re5 manager options:\n\
    --help\n\
    list [full]\n\
    search <query>\n\
    install <package_name>\n\
    remove <package_name>\n\
    version\n\
    update\n\
    upgrade [package]\n\
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
		else if (argc > 3)
			puterror("Invalid argument at position id:4");
		else if (argc == 3)
			puterror("Invalid argument at position id:3, only 'full' or empty accepted");
		if(total_elem == -1)
			puterror("An error occurred while loading apps");
	}
	else if (strcmp(argv[1], "search") == 0)
	{
		if (argc >= 3)
		{
			int total_elem = load_json_repo(0, argv[2]);
			if (total_elem != -1)
				putstrf("%sSuccessfully displayed %i elements from query%s\n", 1, COLOR_YELLOW, total_elem, COLOR_RESET);
			else
				puterror("No result to be shown here");
		}
		else
			puterror("Please enter a search query");
	}
	else if (strcmp(argv[1], "install") == 0)
	{
		if (argc != 3)
		{
			putstrf("%s[%sx%s] Error, format: %s install [package name]%s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, argv[0], COLOR_RESET);
			return (1);
		}
		// try to install package
		if (install(argv[2]))
			putstrf("Successfully installed %s on your system!\n", 1, argv[2]);
		else
		{
			putstrf("%s[%sx%s] Cound not find requested package, try maybe updating using '%s update'%s\n", 2, COLOR_BRED, COLOR_RED, COLOR_BRED, argv[0], COLOR_RESET);
			return (1);
		}
	}
	else if (strcmp(argv[1], "upgrade") == 0)
	{
		if (argc == 2) // upgrade all
		{

		}
		else if (argc == 3) // upgrade package
		{

		}
		else
			puterror("Invalid argument at position id:4");
	}
	else
		puterror("Invalid argument at position id:1");
	return (0);
}
