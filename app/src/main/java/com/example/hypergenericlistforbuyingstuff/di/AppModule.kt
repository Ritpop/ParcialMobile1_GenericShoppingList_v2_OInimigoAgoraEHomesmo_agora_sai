package com.example.hypergenericlistforbuyingstuff.di

import com.example.hypergenericlistforbuyingstuff.features.auth.data.repository.*
import com.example.hypergenericlistforbuyingstuff.features.auth.data.source.*
import com.example.hypergenericlistforbuyingstuff.features.auth.presentation.viewmodel.*
import com.example.hypergenericlistforbuyingstuff.features.category.data.repository.*
import com.example.hypergenericlistforbuyingstuff.features.category.data.source.*
import com.example.hypergenericlistforbuyingstuff.features.category.presentation.viewmodel.CategoryViewModel
import com.example.hypergenericlistforbuyingstuff.features.listitem.data.repository.*
import com.example.hypergenericlistforbuyingstuff.features.listitem.data.source.*
import com.example.hypergenericlistforbuyingstuff.features.listitem.presentation.viewmodel.ListItemsViewModel
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.repository.*
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.data.source.*
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel.ListDetailsViewModel
import com.example.hypergenericlistforbuyingstuff.features.shoppinglist.presentation.viewmodel.ListsViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {

    // Instacias do firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    // Auth
    single { UserDataSource(get()) }
    single<AuthDataSource> { FirebaseAuthDataSource(get()) }
    single<AuthRepository> { AuthRepositoryImpl(get(), get()) }

    // ShoppingListDataSource
    single<StorageDataSource> { FirebaseStorageDataSource(get()) }
    single<ShoppingListDataSource> { FirebaseShoppingListDataSource(get()) }
    single<ShoppingListRepository> { ShoppingListRepositoryImpl(get(), get()) }
    // list items
    single<ListItemDataSource> { FirebaseListItemDataSource(get()) }
    single<ListItemRepository> { ListItemRepositoryImpl(get()) }


    // cateogies
    single<CategoryDataSource> { FirebaseCategoryDataSource(get()) }
    single<CategoryRepository> { CategoryRepositoryImpl(get()) }

    // auth
    viewModel { LoginViewModel(get()) }
    viewModel { RegisterViewModel(get()) }

    viewModel { ListsViewModel(get(), get()) }
    viewModel { ListDetailsViewModel(get(), get()) }
    viewModel { ListItemsViewModel(get(), get()) }

    viewModel { CategoryViewModel(get()) }
}