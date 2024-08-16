' you can write all your functions in this Module or add new ones,
' you can add functions and subs as much as you like,
' the main fuction is "solve" -
' you should add your code inside "solve" function in place of "write your code here". Leave the code before and after as it is.


Option Base 1
'Declaring the bill-of-materials matrix
Dim BOM As Variant
'Declaring the material IDs array
Dim IDs As Variant
'Saving maximum value for an integer variable
Const MAXINT = (2 ^ 15) - 1


Sub solve()
'This sub executes the MRP planning process.
'this is the main function that you should edit
    Application.ScreenUpdating = False
    GetColor = Sheets("Summary").Range("B3").Interior.ColorIndex
    
    ''''''''''''''''''''''''''
    ' write your code here.
    calculateBOM
    setInput
    For i = 1 To 9
        calculatePlan (i)
    Next i
    printSummary
    ''''''''''''''''''''''''''
    
    Sheets("Summary").Activate
    ActiveSheet.Range("A1").Select
    ThisWorkbook.RefreshAll
    Application.ScreenUpdating = True
    
End Sub

Sub calculateBOM()
    Dim P0 As Variant: P0 = Application.Munit(9)
    Dim P1 As Variant: P1 = initP1
    BOM = matrixAddition(P0, P1)
    
    Dim P_i As Variant: P_i = P1
    For i = 2 To 3 'While P_i != Matrix.Zero
        P_i = Application.MMult(P_i, P1)
        BOM = matrixAddition(BOM, P_i)
    Next i
End Sub

Sub setInput()
    IDs = Array("A", "B", "C", "D", "E", "F", "G", "H", "I")
    clearBoards
    For i = 1 To 9
        Call copyVector("DATA_" & IDs(i), IDs(i) & "_DATA")
        Call copyVector("ED_" & IDs(i), IDs(i) & "_ED")
        Call copyVector("X_" & IDs(i), IDs(i) & "_X")

    Next i
End Sub


Sub calculatePlan(i As Integer)
    calcInternalD (i)
    calcR (i)
    calcY (i)
    calcD (i)
    calcV (i)
    calcQ (i)
End Sub

Sub printSummary()
    For i = 1 To 9
        Call copyVector(IDs(i) & "_Q", "Q_" & IDs(i))
    Next i
End Sub

Sub clearBoards()
    'Clear previous summary
    Worksheets("Summary").Range("B3:K11").ClearContents
    'Clear Item tables
    For i = 3 To 11
        Worksheets(i).Range("D3:N3").ClearContents
        Worksheets(i).Range("B6:K15").ClearContents
    Next i
    'Clear assembly lot size methods (only LFL & FP sheets being used in this program)
    Worksheets("LFL").Range("A3:B3").ClearContents
    Worksheets("LFL").Range("B7:K8").ClearContents
    Worksheets("FP").Range("A3:C3").ClearContents
    Worksheets("FP").Range("B7:K8").ClearContents
End Sub

Sub calcInternalD(i As Integer)
    Dim sum As Integer
    For t = 1 To 10 'No. of periods
        For j = 1 To 5 'No. of assembly items
            Dim Qj As Integer: Qj = Range(IDs(j) & "_Q").Cells(1, t)
            sum = sum + BOM(j, i) * Qj
        Next j
        Range(IDs(i) & "_ID").Cells(1, t) = sum
        sum = 0
    Next t
End Sub

Sub calcR(i As Integer)
    Dim sum As Integer
    For t = 1 To 10
        Dim ED As Integer: ED = Range(IDs(i) & "_ED").Cells(1, t)
        Dim id As Integer: id = Range(IDs(i) & "_ID").Cells(1, t)
        Range(IDs(i) & "_R").Cells(1, t) = ED + id
    Next t
End Sub

Sub calcY(i As Integer)
    Dim OBJ_1 As Integer: OBJ_1 = Range(IDs(i) & "_SS")
    For t = 1 To 10
        Dim y_prev As Integer
        If t > 1 Then
            y_prev = Range(IDs(i) & "_Y").Cells(1, t - 1)
        Else
            y_prev = Range(IDs(i) & "_OH")
        End If
        Dim OBJ_2 As Integer: OBJ_2 = y_prev - Range(IDs(i) & "_R").Cells(1, t) + Range(IDs(i) & "_X").Cells(1, t)
        Dim y As Integer: y = Application.Max(OBJ_1, OBJ_2)
        Range(IDs(i) & "_Y").Cells(1, t) = y
    Next t
End Sub

Sub calcD(i As Integer)
    Dim SS As Integer: SS = Range(IDs(i) & "_SS")
    For t = 1 To 10
        Dim sum As Integer
        Dim y As Integer: y = Range(IDs(i) & "_Y").Cells(1, t)
        If y = SS Then
            Dim r As Integer: r = Range(IDs(i) & "_R").Cells(1, t)
            Dim x As Integer: x = Range(IDs(i) & "_X").Cells(1, t)
            Dim y_prev As Integer
            If t > 1 Then
                y_prev = Range(IDs(i) & "_Y").Cells(1, t - 1)
            Else
                y_prev = Range(IDs(i) & "_OH")
            End If
            sum = SS + r - y_prev - x
        Else
            sum = 0
        End If
        Range(IDs(i) & "_D").Cells(1, t) = sum
    Next t
End Sub

Sub calcV(i As Integer)
    Dim LT As Integer: LT = Range(IDs(i) & "_LT")
    Dim sum As Integer
    For t = 1 To LT + 1
        sum = sum + Range(IDs(i) & "_D").Cells(1, t)
    Next t
    Range(IDs(i) & "_V").Cells(1, 1) = sum
    For t = 2 To 10 - LT
        Range(IDs(i) & "_V").Cells(1, t) = Range(IDs(i) & "_D").Cells(1, t + LT)
    Next t
End Sub

Sub calcQ(i As Integer)
    If i >= 6 Then
        LP (i)
    Else
        Dim LotSize As String: LotSize = Range(IDs(i) & "_LS")
        Select Case LotSize
            Case "LFL"
                LFL (i)
            Case "FP"
                FP (i)
            Case "SM"
                SM (i)
            Case "WW"
                WW (i)
            End Select
    End If
End Sub

Sub LFL(i As Integer)
    'Call copyVector(IDs(i) & "_V", IDs(i) & "_Q")    '<-  one-line alternative
    
    Range("LFL_k") = Range(IDs(i) & "_DATA").Cells(1, 5)   'unused
    Range("LFL_h") = Range(IDs(i) & "_DATA").Cells(1, 7)   'unused
    Call copyVector(IDs(i) & "_V", "LFL_V")
    Call copyVector("LFL_V", "LFL_Q")
    Call copyVector("LFL_Q", IDs(i) & "_Q")
    
    'Clear
    Worksheets("LFL").Range("A3:B3").ClearContents
    Worksheets("LFL").Range("B7:K8").ClearContents
End Sub

Sub FP(i As Integer)
    Range("FP_k") = Range(IDs(i) & "_DATA").Cells(1, 5)    'unused
    Range("FP_h") = Range(IDs(i) & "_DATA").Cells(1, 7)    'unused
    Range("FP_size") = Range(IDs(i) & "_DATA").Cells(1, 6)
    Call copyVector(IDs(i) & "_V", "FP_V")
    
    Dim sum As Integer
    Dim t As Integer: t = 1
    For t = 1 To 10 Step Range("FP_size")   'periods we can order in
        For k = 0 To Range("FP_size") - 1   'in-between periods to take care of
            sum = sum + Range("FP_V").Cells(1, t + k)
        Next k
        Range("FP_Q").Cells(1, t) = sum
        sum = 0
    Next t
    
    Call copyVector("FP_Q", IDs(i) & "_Q")
    
    'Clear
    Worksheets("FP").Range("A3:C3").ClearContents
    Worksheets("FP").Range("B7:K8").ClearContents
End Sub

Sub SM(i As Integer)
    Dim k As Double: k = Range(IDs(i) & "_DATA").Cells(1, 5)
    Dim h As Double: h = Range(IDs(i) & "_DATA").Cells(1, 7)
    Dim V As Variant: V = Range(IDs(i) & "_V")
    Dim avg As Double           'current average cost per period
    Dim avg_prev As Double      'previous average cost per period
    Dim total As Double        'total cost for the period range
    Dim t1 As Integer: t1 = 1   'starting period
    Dim n As Integer            'number of periods currently
    Dim orders(1 To 10) As Integer  '1 on periods we order in, 0 otherwise
    'Dim orderTo(1 To 10) As Integer
    'Dim inventory(1 To 10) As Integer
    
    Do While t1 <= 10
        avg_prev = k
        For t2 = t1 + 1 To 10
            n = t2 - t1 + 1
            avg = (((avg_prev * (n - 1)) + (V(1, t2) * h * (n - 1))) / n)
            If avg > avg_prev Then
                orders(t1) = 1
                t1 = t2
                Exit For
            End If
            If t2 = 10 Then
                orders(t1) = 1
                t1 = t2
            End If
            avg_prev = avg
        Next t2
        If t1 = 10 Then
            Exit Do
        End If
    Loop
    
    Dim Q(1 To 10) As Integer
    Dim start As Integer
    For t = 1 To 10
        If orders(t) = 1 Then
            start = t
        End If
        Q(start) = Q(start) + V(1, t)
    Next t
    
    For j = 1 To 10
        Range(IDs(i) & "_Q").Cells(1, j) = Q(j)
    Next j
    
End Sub

Sub WW(id As Integer)
    Dim k As Double: k = Range(IDs(id) & "_DATA").Cells(1, 5)
    Dim h As Double: h = Range(IDs(id) & "_DATA").Cells(1, 7)
    Dim invtCost As Double
    Dim V As Variant: V = Range(IDs(id) & "_V")
    Dim F(0 To 10) As Double
    'Dim F_prev(0 To 10) As Integer
    Dim F_opt As Integer    'index 'i' indicating current first Fi+Mij to check (based on elimination rule #2)
    Dim FM As Double '
    Dim minFMValues(0 To 10) As Integer
    Dim minFMIndexes(0 To 10) As Integer
    Dim Q(1 To 10) As Integer
    
    F(0) = 0
    F_opt = 0
    For i = 1 To 10     'Over F(1,2,..,10)
        If V(1, i) = 0 Then
            F(i) = F(i - 1) 'Elimination rule #1
            minFMValues(i) = minFMValues(i - 1)
            minFMIndexes(i) = minFMIndexes(i - 1)
        Else
            minFMValues(i) = MAXINT
            For j = F_opt To i - 1  'Over possible FM Values
                If V(1, j + 1) = 0 Then
                    GoTo NextIteration
                End If
                For t = j + 2 To i  'Over Vt(...)
                    invtCost = invtCost + V(1, t) * (t - j - 1)
                Next t
                FM = F(j) + k + invtCost
                If FM <= minFMValues(i) Then
                    minFMValues(i) = FM
                    F(i) = FM
                    minFMIndexes(i) = j
                    F_opt = j
                invtCost = 0
                End If
NextIteration:
            Next j
        End If
    Next i
    
    i = 10
    Do While i >= 0
        t = minFMIndexes(i) + 1
        For j = t To i
            Q(t) = Q(t) + V(1, j)
        Next j
        If i = 0 Then
            Exit Do
        End If
        i = minFMIndexes(i)
    Loop
    
    For t = 1 To 10
        Range(IDs(id) & "_Q").Cells(1, t) = Q(t)
    Next t
End Sub

Sub LP(i As Integer)
    solverReset
    setLP (i)
    Worksheets("LP").Activate
    SolverOK SetCell:="LP_Objective", MaxMinVal:=2, ByChange:="LP_vars", Engine:="3"
    SolverAdd CellRef:="LP_B1", Relation:=3, FormulaText:="LP_Positive"
    SolverAdd CellRef:="LP_B2", Relation:=3, FormulaText:="LP_Positive"
    SolverAdd CellRef:="LP_Q", Relation:=3, FormulaText:="LP_Positive"
    SolverAdd CellRef:="LP_I", Relation:=3, FormulaText:="LP_Positive"
    SolverAdd CellRef:="LP_B1", Relation:=4
    SolverAdd CellRef:="LP_B2", Relation:=4
    SolverAdd CellRef:="LP_I0", Relation:=2, FormulaText:="0"
    SolverAdd CellRef:="LP_V0", Relation:=2, FormulaText:="0"
    SolverSolve Userfinish:=True
    Call copyVector("LP_B1_SOLUTION", IDs(i) & "_B1")
    Call copyVector("LP_B2_SOLUTION", IDs(i) & "_B2")
    Call copyVector("LP_Q_SOLUTION", IDs(i) & "_Q")
    Range("LP_INPUT").ClearContents
End Sub


Sub setLP(i As Integer)
    'Copy transposed ranges of data and Vt
    For j = 1 To 10
        Range("LP_V").Cells(j, 1) = Range(IDs(i) & "_V").Cells(1, j)
    Next j
    For j = 1 To 8
        Range("LP_DATA").Cells(j, 1) = Range(IDs(i) & "_DATA").Cells(1, j + 3)
    Next j
End Sub

Sub copyVector(sourceName As String, destName As String)
'This sub copies a source vector to the destination - you can use it!!!
    For i = 1 To 11 '1 to 10 wasn't enough to copy raw material data
        Range(destName).Columns(i).Value = Range(sourceName).Columns(i)
    Next i
End Sub

Sub clearAll()
    Application.ScreenUpdating = False
    Range("B3:H7").Select
    Selection.ClearContents
    Range("C11:M14").Select
    Selection.ClearContents
    Range("B18:K26").Select
    Selection.ClearContents
    Range("B30:K38").Select
    Selection.ClearContents
    
    Range("A1").Select
    Application.ScreenUpdating = True
End Sub

'Initializing P1 values according to the assignment instructions
Function initP1()
    Dim P1(1 To 9, 1 To 9) As Integer
    P1(1, 4) = 1
    P1(1, 5) = 1
    P1(2, 4) = 1
    P1(2, 5) = 2
    P1(3, 4) = 2
    P1(3, 5) = 3
    P1(3, 9) = 1
    P1(4, 8) = 4
    P1(5, 6) = 3
    P1(5, 7) = 2
    initP1 = P1
End Function

Function matrixAddition(m1 As Variant, m2 As Variant)
    'Dim result(UBound(m1, 1) - LBound(m1, 1) + 1, UBound(m1, 2) - LBound(m1, 2) + 1)
    Dim result(1 To 9, 1 To 9) As Integer
    For i = LBound(m1, 1) To UBound(m1, 1)
    'For i = 1 To 9
        For j = LBound(m1, 2) To UBound(m1, 2)
        'For j = 1 To 9
            result(i, j) = m1(i, j) + m2(i, j)
        Next j
    Next i
    matrixAddition = result
End Function
