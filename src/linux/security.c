#include "defs.h"
#include "config.h"
#include <openssl/evp.h>

#define FORBIDDEN_CHARACTERS "/~$:;!?%*^`\\"
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

/*
	Verify SHA256 checksum of a file.
	Returns 1 if match, 0 if mismatch or error.
*/
int verify_checksum(const char *filepath, const char *expected_checksum)
{
	unsigned char hash[EVP_MAX_MD_SIZE];
	unsigned int hash_len;
	EVP_MD_CTX *mdctx;

	if((mdctx = EVP_MD_CTX_new()) == NULL)
		return 0;

	if(1 != EVP_DigestInit_ex(mdctx, EVP_sha256(), NULL)) {
		EVP_MD_CTX_free(mdctx);
		return 0;
	}
	
	FILE *file = fopen(filepath, "rb");
	if (!file) {
		EVP_MD_CTX_free(mdctx);
		return 0;
	}
	
	const int bufSize = 32768;
	unsigned char *buffer = malloc(bufSize);
	if (!buffer) { 
		fclose(file); 
		EVP_MD_CTX_free(mdctx);
		return 0; 
	}
	
	int bytesRead = 0;
	while ((bytesRead = fread(buffer, 1, bufSize, file))) {
		if(1 != EVP_DigestUpdate(mdctx, buffer, bytesRead)) {
			fclose(file);
			free(buffer);
			EVP_MD_CTX_free(mdctx);
			return 0;
		}
	}

	if(1 != EVP_DigestFinal_ex(mdctx, hash, &hash_len)) {
		fclose(file);
		free(buffer);
		EVP_MD_CTX_free(mdctx);
		return 0;
	}
	
	fclose(file);
	free(buffer);
	EVP_MD_CTX_free(mdctx);
	
	char outputBuffer[65];
	for(unsigned int i = 0; i < hash_len; i++) {
		sprintf(outputBuffer + (i * 2), "%02x", hash[i]);
	}
	outputBuffer[64] = 0;

	if (strncmp(expected_checksum, "sha256:", 7) == 0 && expected_checksum[7]) // check if signature match
		return strcmp(outputBuffer, expected_checksum + 7) == 0;
	else
		return 0;
}
