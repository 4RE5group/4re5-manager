#include "defs.h"

size_t	write_callback(void *contents, size_t size, size_t nmemb, void *userp)
{
	size_t realsize = size * nmemb;
	FILE *fp = (FILE *)userp;
	return (fwrite(contents, 1, realsize, fp));
}

int	fetch_url(char *url, char *output_file)
{
	CURL *curl = NULL;
	CURLcode res;
	FILE *fp = NULL;
	int result_code = 1; // Default to error

	curl_global_init(CURL_GLOBAL_DEFAULT);
	curl = curl_easy_init();

	//putstrf("fetching url: '%s' to '%s'\n", 1, url, output_file);
	if (!curl)
	{
		putstrf("%s[%sx%s] Failed to initialize libcurl\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED);
		goto cleanup;
	}

	fp = fopen(output_file, "wb");
	if (!fp)
	{
		putstrf("%s[%sx%s] Could not open output file: %s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, output_file);
		goto cleanup;
	}

	curl_easy_setopt(curl, CURLOPT_URL, url);
	curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, write_callback);
	curl_easy_setopt(curl, CURLOPT_WRITEDATA, fp);
	curl_easy_setopt(curl, CURLOPT_FOLLOWLOCATION, 1L); // Follow redirects

	res = curl_easy_perform(curl);
	if (res != CURLE_OK)
		putstrf("%s[%sx%s] curl_easy_perform() failed: %s\n", 2, COLOR_RED, COLOR_BRED, COLOR_RED, curl_easy_strerror(res));
	else
		result_code = 0; // Success
cleanup:
	if (fp)		fclose(fp);
	if (curl)	curl_easy_cleanup(curl);
	curl_global_cleanup();
	return (result_code);
}
