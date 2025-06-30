import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../../l10n/app_localizations.dart';
import '../model/User.dart';
import '../viewmodel/HomeViewModel.dart';

class UserListTile extends StatelessWidget {
  final User user;
  const UserListTile({super.key, required this.user});

  @override
  Widget build(BuildContext context) {
    var profileImagePath = user.profileImagePath ?? '';
    final file = File(profileImagePath);

    return InkWell(
      onLongPress: () async {
        final RenderBox renderBox = context.findRenderObject() as RenderBox;
        final position = renderBox.localToGlobal(Offset.zero);
        final size = renderBox.size;

        final selected = await showMenu<String>(
          context: context,
          position: RelativeRect.fromLTRB(
            position.dx + size.width - 40,
            position.dy + size.height,
            0,
            0,
          ),
          items: [
            PopupMenuItem<String>(
              value: 'delete',
              child: Text(AppLocalizations.of(context)!.deletePopupTitle),
            ),
          ],
        );
        if (selected == 'delete') {
          await Provider.of<HomeViewModel>(
            context,
            listen: false,
          ).deleteUser(user);
        }
      },
      child: ListTile(
        leading: ProfilePicture(
          profileImagePath: profileImagePath,
          file: file,
          platform: defaultTargetPlatform,
        ),
        title: Text(user.name),
        subtitle: Text(user.phoneNumber),
        trailing: UserSignature(signatureBase64: user.signatureBase64),
      ),
    );
  }
}

class ProfilePicture extends StatelessWidget {
  final String profileImagePath;
  final File file;
  final TargetPlatform platform;

  const ProfilePicture({
    required this.profileImagePath,
    required this.file,
    required this.platform,
    super.key,
  });

  @override
  Widget build(BuildContext context) {
    if (platform == TargetPlatform.android &&
        profileImagePath.isNotEmpty &&
        file.existsSync()) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(20),
        child: Image.file(file, width: 40, height: 40, fit: BoxFit.cover),
      );
    } else {
      return ClipRRect(
        borderRadius: BorderRadius.circular(20),
        child: Container(
          width: 40,
          height: 40,
          color: Colors.grey,
          child: const Icon(Icons.person, color: Colors.white),
        ),
      );
    }
  }
}

class UserSignature extends StatelessWidget {
  final String? signatureBase64;

  const UserSignature({this.signatureBase64, super.key});

  @override
  Widget build(BuildContext context) {
    if (signatureBase64 != null && signatureBase64!.isNotEmpty) {
      try {
        String cleanedBase64 = signatureBase64!.replaceAll(RegExp(r'\s+'), '');
        return Image.memory(
          base64Decode(cleanedBase64),
          width: 60,
          height: 40,
          fit: BoxFit.contain,
        );
      } catch (e) {
        return const Icon(Icons.error, color: Colors.red);
      }
    } else {
      return const SizedBox.shrink();
    }
  }
}
