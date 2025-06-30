import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../l10n/app_localizations.dart';
import '../viewmodel/HomeViewModel.dart';
import 'UserListTile.dart';

class HomeScreen extends StatelessWidget {
  const HomeScreen({super.key});

  @override
  Widget build(BuildContext context) {
    final userViewModel = Provider.of<HomeViewModel>(context);
    final users = userViewModel.users;
    final isLoading = userViewModel.isLoading;

    return Scaffold(
      appBar: AppBar(
        title: Text(
          AppLocalizations.of(context)!.appTitle,
          style: const TextStyle(fontWeight: FontWeight.w400),
        ),
      ),

      body: isLoading
          ? const Center(child: CircularProgressIndicator())
          : users.isEmpty
          ? Center(child: Text(AppLocalizations.of(context)!.noUsers))
          : ListView.builder(
        itemCount: users.length,
        itemBuilder: (context, index) {
          final user = users[index];
          return UserListTile(user: user);
        },
      ),

      floatingActionButton: FloatingActionButton(
        onPressed: () {
          if(Theme.of(context).platform == TargetPlatform.android) {
            Provider.of<HomeViewModel>(context, listen: false).addUser();
          } else {
            ScaffoldMessenger.of(context).showSnackBar(
              SnackBar(content: Text(AppLocalizations.of(context)!.unSupportedMessage))
            );
          }
        },
        tooltip: 'Add User',
        child: const Icon(Icons.add),
      ),
    );
  }
}
