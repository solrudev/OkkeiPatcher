package ru.solrudev.okkeipatcher.data.service;

import ru.solrudev.okkeipatcher.data.service.IBinaryPatchCallback;

interface IBinaryPatchService {
    void destroy() = 16777114;
    void patchAsync(String inputPath, String outputPath, String diffPath, IBinaryPatchCallback callback) = 1;
    void exit(int status) = 2;
}