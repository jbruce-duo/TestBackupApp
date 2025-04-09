package com.example.testbackupapp

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.app.backup.FullBackupDataOutput
import android.os.ParcelFileDescriptor
import android.util.Log
import java.io.File
import java.io.FileWriter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class TestBackupAgent: BackupAgent() {

    override fun onFullBackup(data: FullBackupDataOutput?) {
        if (data == null) {
            Log.d("TestBackupAgent", "onFullBackup called with null data, exiting")
            return
        }

        val encryptionEnabled = data.transportFlags and FLAG_CLIENT_SIDE_ENCRYPTION_ENABLED > 0
        val deviceTransfer = data.transportFlags and FLAG_DEVICE_TO_DEVICE_TRANSFER > 0
        Log.d("TestBackupAgent", "onFullBackup called with flags: ${data.transportFlags} encryptionEnabled: $encryptionEnabled, deviceTransfer: $deviceTransfer")

        val someBackupFile = File(applicationContext.filesDir, "someFile.txt")
        FileWriter(someBackupFile).use { writer ->
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm a")
            val currentDateTime = LocalDateTime.now().format(formatter)
            writer.write("This is the contents of a test backup file created at $currentDateTime. During backup, flags were ${data.transportFlags} and encryptionEnabled was $encryptionEnabled and deviceTransfer was $deviceTransfer")
        }
        fullBackupFile(someBackupFile, data)
        Log.d("TestBackupAgent", "backup file written to ${someBackupFile.absolutePath}")
    }

    override fun onRestoreFile(
        data: ParcelFileDescriptor?,
        size: Long,
        destination: File?,
        type: Int,
        mode: Long,
        mtime: Long
    ) {
        Log.d("TestBackupAgent", "onRestoreFile called with file ${destination?.name} with size $size")
        super.onRestoreFile(data, size, destination, type, mode, mtime)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d("TestBackupAgent", "onCreate called")
    }

    override fun onBackup(
        oldState: ParcelFileDescriptor?,
        data: BackupDataOutput?,
        newState: ParcelFileDescriptor?
    ) {
        Log.d("TestBackupAgent", "onBackup called")
    }

    override fun onRestore(
        data: BackupDataInput?,
        appVersionCode: Int,
        newState: ParcelFileDescriptor?
    ) {
        Log.d("TestBackupAgent", "onRestore called")
    }
}