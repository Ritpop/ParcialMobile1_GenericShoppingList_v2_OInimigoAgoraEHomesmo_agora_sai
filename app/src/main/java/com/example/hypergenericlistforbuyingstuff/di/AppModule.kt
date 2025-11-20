package com.example.hypergenericlistforbuyingstuff.di

import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepository
import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.AuthRepositoryImpl
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.AuthDataSource
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.FirebaseAuthDataSource
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }



    single<AuthDataSource> { FirebaseAuthDataSource(get(), get()) }

    single<AuthRepository> { AuthRepositoryImpl(get()) }


    viewModel { AuthViewModel(get()) }
}