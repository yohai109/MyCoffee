package com.yohai.mycoffee

enum class ExportFormat { CSV, JSON }
enum class ExportScope { ALL, STOCK, BREWS }

expect fun saveExportFile(filename: String, content: String): Boolean
expect fun shareExportFile(filename: String, content: String): Boolean
expect fun notifyTimerComplete()
