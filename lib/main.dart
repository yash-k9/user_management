import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import 'package:user_management/l10n/app_localizations.dart';
import 'package:user_management/l10n/l10n.dart';
import 'package:user_management/utils/AppTheme.dart';
import 'package:flutter/services.dart';

import 'home/data/IUserRepository.dart';
import 'home/ui/HomeScreen.dart';
import 'home/viewmodel/HomeViewModel.dart';
import 'home/data/UserRepositoryImpl.dart';

void main() {
  runApp(
    MultiProvider(
      providers: [
        Provider<IUserRepository>(
          create: (_) => UserRepositoryImpl(
            platformMethodChannel: const MethodChannel("nativeChannel"),
            userEventChannel: const EventChannel('userStreamChannel'),
          ),
        ),
        ChangeNotifierProvider<HomeViewModel>(
          create: (context) => HomeViewModel(Provider.of<IUserRepository>(context, listen: false)),
        ),
      ],
      child: const App(),
    ),
  );
}

class App extends StatelessWidget {
  const App({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      debugShowCheckedModeBanner: false,
      supportedLocales: L10n.all,
      localizationsDelegates: AppLocalizations.localizationsDelegates,
      locale: const Locale('en'),
      themeMode: ThemeMode.system,
      theme: AppTheme.lightTheme,
      darkTheme: AppTheme.darkTheme,
      home: const HomeScreen(),
    );
  }
}
