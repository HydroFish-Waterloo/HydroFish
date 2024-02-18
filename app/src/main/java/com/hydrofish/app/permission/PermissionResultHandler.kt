package com.hydrofish.app.permission

interface PermissionResultHandler {
    fun handlePermissionResult(isGranted: Boolean): Boolean
}


class RealPermissionResultHandler : PermissionResultHandler {
    override fun handlePermissionResult(isGranted: Boolean): Boolean {
        return isGranted
    }
}
