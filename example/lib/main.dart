import 'dart:io';

import 'package:cameraplugin/cameraplugin.dart';
import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  MyAppDataModel _myAppDataModel;

  @override
  void initState() {
    _myAppDataModel = MyAppDataModel();
    _myAppDataModel.inputClickState.add([]);
    super.initState();
  }



  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
              mainAxisSize: MainAxisSize.min,
              mainAxisAlignment: MainAxisAlignment.center,
              children: <Widget>[
                StreamBuilder<List<String>>(
                  initialData: [],
                  stream: _myAppDataModel.outputResult,
                  builder: (context, snapshot) => Padding(
                    padding: EdgeInsets.only(
                      left: 8,
                      right: 8,
                      top: 12,
                      bottom: 24,
                    ),
                    child: snapshot.hasData
                        ? snapshot.data.isNotEmpty
                        ? ClipRRect(
                      borderRadius: BorderRadius.circular(24),
                      child: Image.file(
                        File(snapshot.data[0]),
                        fit: BoxFit.cover,
                        width:
                        MediaQuery.of(context).size.width * .75,
                        height: MediaQuery.of(context).size.height *
                            .35,
                      ),
                    )
                        : Center()
                        : CircularProgressIndicator(),
                  ),

                ),
                FlatButton(child:Text("click"), onPressed: (){
                  Cameraplugin.takePhoto().then(
                        (data) => _myAppDataModel.inputClickState.add(data),
                    onError: (e) =>
                        _myAppDataModel.inputClickState.add([e.toString()]),
                  );
                }),
              ]
          ),
        ),
      ),
    );
  }
}


class MyAppDataModel {
  StreamController<List<String>> _streamController = StreamController<List<String>>.broadcast();

  Sink<List<String>> get inputClickState => _streamController;

  Stream<List<String>> get outputResult => _streamController.stream.map((data) => data);

  dispose() => _streamController.close();
}