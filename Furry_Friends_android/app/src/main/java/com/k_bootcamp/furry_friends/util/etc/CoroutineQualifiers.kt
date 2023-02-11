package com.k_bootcamp.furry_friends.util.etc

import javax.inject.Qualifier

// dispatcher를 구분할 한정자 annotation

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class IoDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class MainDispatcher

@Retention(AnnotationRetention.RUNTIME)
@Qualifier
annotation class DefaultDispatcher
