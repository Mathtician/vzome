﻿using System;
using System.Collections.Generic;
using System.Text;
using System.IO;
using UnityEngine;
using UnityEngine.UI;
using Unity.Jobs;
using Unity.Collections;

public class VZomeJavaBridge : MonoBehaviour
{
    private IDictionary<string, Mesh> meshes = new Dictionary<string, Mesh>();
    private IDictionary<string, Material> materials = new Dictionary<string, Material>();
    private IDictionary<string, GameObject> instances = new Dictionary<string, GameObject>();

    private Text msgText;
    private GameObject template;

    public Transform canvas;
    public Dropdown dropdown;
    int selectedFile;

    private const string VZOME_PATH = "/mnt/sdcard/Oculus/vZome/";
    private const string VZOME_EXTENSION = ".vzome";
    private List<string> fileNames = new List<string>();
    private List<string> paths = new List<string>();

    private AndroidJavaClass adapterClass;
    private AndroidJavaObject adapter;

    void Start()
    {
        adapterClass = new AndroidJavaClass( "com.vzome.unity.Adapter" );
        adapterClass .CallStatic( "initialize", new AndroidJavaClass( "com.unity3d.player.UnityPlayer" ) );

        UnityEngine.Object[] shapes = Resources.LoadAll( "Shapes", typeof(TextAsset) );
        foreach ( var vef in shapes )
        {
            Debug.Log( "%%%%%%%%%%%%%% loaded shape VEF " + vef.name );
            adapterClass .CallStatic( "registerShape", vef.name, ((TextAsset) vef).text );
        }

        foreach (string path in Directory .GetFiles( VZOME_PATH ) )
        {
            string ext = Path .GetExtension( path ) .ToLower();
            if ( VZOME_EXTENSION .Equals( ext ) ) {
                string filename = Path .GetFileNameWithoutExtension( path );
                fileNames .Add( filename );
                paths .Add( path );
            }
        }
        dropdown .AddOptions( fileNames );
        DropdownIndexChanged( 0 );
    }

    public void DropdownIndexChanged( int index )
    {
        selectedFile = index;
    }

    public void UndoAll()
    {
        Debug.Log( "%%%%%%%%%%%%%% UndoAll triggered" );
        if ( adapter != null ) {
            Debug.Log( "%%%%%%%%%%%%%% UndoAll has adapter" );
            adapter .Call( "doAction", "undoAll" );
        }
    }

    public void LoadVZome()
    {
        template = this .transform .Find( "vZomeTemplate" ) .gameObject;

        Transform panel = canvas .Find( "Panel" );
        GameObject messages = panel .Find( "JavaMessages" ) .gameObject;

        msgText = messages .GetComponent<Text>();
        msgText .text = "Loading file: " + fileNames[ selectedFile ];
        LoadVZomeJob job = new LoadVZomeJob();

        string filePath = paths[ selectedFile ];
        job .pathN = new NativeArray<byte>( filePath.Length, Allocator.Temp );
        job .pathN .CopyFrom( Encoding.ASCII.GetBytes( filePath ) );

        string anchor = this .name;
        job .objectNameN = new NativeArray<byte>( anchor.Length, Allocator.Temp );
        job .objectNameN .CopyFrom( Encoding.ASCII.GetBytes( anchor ) );

        JobHandle jh = job .Schedule();
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob scheduled. " );
    }

    void AdapterReady( string path )
    { 
        adapter = adapterClass .CallStatic<AndroidJavaObject>( "getAdapter", path );
        Debug.Log( "%%%%%%%%%%%%%% AdapterReady got the adapter: " + adapter .ToString() );
    }

    void SetLabelText( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% SetLabelText from Java: " + message );
        msgText .text = message;
    }

    void LogInfo( string message )
    { 
        Debug.Log( "%%%%%%%%%%%%%% From Java: " + message );
        msgText .text = message;
    }

    void LogException( string message )
    { 
        Debug.LogError( "%%%%%%%%%%%%%% From Java: " + message );
        msgText .text = message;
    }

    void DefineMesh( string json )
    {
        Shape shape = JsonUtility.FromJson<Shape>(json);
        Mesh mesh = shape .ToMesh();
        Debug.Log( "%%%%%%%%%%%%%% DefineMesh: mesh created: " + mesh.vertices[0] );
        meshes .Add( shape .id, mesh );
    }

    void CreateGameObject( string json )
    { 
        Instance instance = JsonUtility.FromJson<Instance>(json);
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject from Java: " + instance.id );
        GameObject copy = Instantiate( template );
        MeshRenderer meshRenderer = copy .AddComponent<MeshRenderer>();

        Material material;
        if ( materials .ContainsKey( instance .color ) ) {
            material = materials[ instance .color ];
        } else {
            material = new Material( Shader.Find("Standard") );
            Debug.Log( "&&&&& material created for " + instance.color );
            Color color;
            ColorUtility .TryParseHtmlString( instance .color, out color );
            material .color = color;
            materials .Add( instance .color, material );
        }
        meshRenderer.sharedMaterial = material;

        MeshFilter meshFilter = copy .AddComponent<MeshFilter>();
        meshFilter.mesh = meshes[ instance .shape ];

        MeshCollider collider = copy .GetComponent<MeshCollider>();
        collider .sharedMesh = meshFilter .mesh;

        copy .transform .localPosition = instance .position;
        copy .transform .localRotation = instance .rotation;
        copy .transform .SetParent( this .transform, false );

        instances .Add( instance .id, copy );
        Debug.Log( "%%%%%%%%%%%%%% CreateGameObject done!" );
    }

    void DeleteGameObject( string json )
    { 
        Deletion deletion = JsonUtility.FromJson<Deletion>(json);
        Debug.Log( "%%%%%%%%%%%%%% DeleteGameObject from Java: " + deletion .id );
        Destroy( instances[ deletion .id ] );
        instances .Remove( deletion .id );
    }

    [Serializable]
    public class Shape
    {
        public string id;
        public Vector3[] tvertices;
        public Vector3[] normals;
        public int[] triangles;

        public Mesh ToMesh()
        {
            Mesh mesh = new Mesh();
            mesh.vertices = this.tvertices;
            mesh.triangles = this.triangles;
            mesh.normals = this.normals;
            return mesh;
        }
    }

    [Serializable]
    public struct Deletion
    {
        public string id;
    }

    [Serializable]
    public struct Instance
    {
        public string id;
        public string shape;
        public string color;
        public Vector3 position;
        public Quaternion rotation;
    }
}

public struct LoadVZomeJob : IJob
{
    public NativeArray<byte> pathN;
    public NativeArray<byte> objectNameN;
    
    public void Execute()
    {
        string path = Encoding.ASCII.GetString( pathN .ToArray() );
        pathN .Dispose();

        string objectName = Encoding.ASCII.GetString( objectNameN .ToArray() );
        objectNameN .Dispose();

        AndroidJNI.AttachCurrentThread();

        AndroidJavaClass adapterClass = new AndroidJavaClass( "com.vzome.unity.Adapter" );
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob attempting to open: " + path );
        adapterClass .CallStatic( "loadFile", path, objectName );
        Debug.Log( "%%%%%%%%%%%%%% LoadVZomeJob: loadFile returned" );
    }
}
